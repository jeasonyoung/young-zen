package org.young.common.lock;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.util.Assert;
import org.young.common.protocol.Constants;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Redis分布式锁
 * 使用 SET resource-name anystring NX EX max-lock-time 实现
 * <p>
 *     该方案在 Redis 官方 SET 命令也有详细介绍
 *     http://doc.redisfans.com/string/set.html
 * </p>
 * 在介绍该分布式锁设计之前，我们先来看一下在从 Redis 2.6.12 开始 SET 提供的新特性
 * 命令 SET key value [EX seconds] [PX milliseconds] [NX|XX]，其中：
 * <p>
 *     EX seconds — 以秒为单位设置 key 的过期时间；
 *     PX milliseconds — 以毫秒为单位设置 key 的过期时间；
 *     NX — 将key 的值设为value ，当且仅当key 不存在，等效于 SETNX。
 *     XX — 将key 的值设为value ，当且仅当key 存在，等效于 SETEX。
 * </p>
 * 命令 SET resource-name anystring NX EX max-lock-time 是一种在 Redis 中实现锁的简单方法。
 * <p>
 *     客户端执行以上的命令：
 * </p>
 * <p>
 *     如果服务器返回 OK ，那么这个客户端获得锁。
 *     如果服务器返回 NIL ，那么客户端获取锁失败，可以在稍后再重试。
 * </p>
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/9/10 14:41
 */
@Slf4j
@Data
public class RedisLock {
    /**
     * 随机数产生器
     */
    private static final Random RANDOM = new Random();
    /**
     * 默认锁的有效时间(s)
     */
    public static final int EXPIRE = 60;
    /**
     * 默认请求锁的超时时间(ms 毫秒)
     */
    private static final long TIME_OUT = 100;

    /**
     * 解锁的lua脚本
     */
    public static final String UNLOCK_LUA;

    static {
        UNLOCK_LUA = "if redis.call(\"get\",KEYS[1]) == ARGV[1] " +
                "then " +
                "    return redis.call(\"del\",KEYS[1]) " +
                "else " +
                "    return 0 " +
                "end ";
    }

    /**
     * 锁标记
     */
    private final AtomicBoolean locked = new AtomicBoolean(false);

    /**
     * 锁的有效时间(s)
     */
    private int expireTime = EXPIRE;
    /**
     * 请求锁的超时时间(ms)
     */
    private long timeOut = TIME_OUT;

    /**
     * 锁标志对应的key
     */
    private final String lockKey;
    /**
     * 锁标志对应的值
     */
    private String lockValue;
    /**
     * redisTemplate
     */
    private final RedisTemplate<Object, Object> redisTemplate;

    /**
     * 使用默认的锁过期时间和请求锁的超时时间
     * @param redisTemplate
     * redisTemplate
     * @param lockKey
     * 锁的key(Redis的Key)
     */
    public RedisLock(@Nonnull final RedisTemplate<Object, Object> redisTemplate,@Nonnull final String lockKey){
        log.debug("RedisLock(redisTemplate: {}, lockKey: {})...", redisTemplate, lockKey);
        this.redisTemplate = redisTemplate;
        this.lockKey = lockKey;
    }

    /**
     * 使用默认的请求锁的超时时间，指定锁的过期时间
     * @param redisTemplate
     * redisTemplate
     * @param lockKey
     * 锁的key(Redis的Key)
     * @param expireTime
     * 锁的过期时间(单位：秒)
     */
    public RedisLock(@Nonnull final RedisTemplate<Object, Object> redisTemplate,@Nonnull final String lockKey, int expireTime){
        this(redisTemplate, lockKey);
        log.debug("RedisLock(expireTime: {})...", expireTime);
        this.setExpireTime(expireTime);
    }

    /**
     * 使用默认的锁的过期时间，指定请求锁的超时时间
     * @param redisTemplate
     * redisTemplate
     * @param lockKey
     * 锁的key(Redis的Key)
     * @param timeOut
     * 请求锁的超时时间(单位：毫秒)
     */
    public RedisLock(@Nonnull final RedisTemplate<Object, Object> redisTemplate,@Nonnull final String lockKey, long timeOut){
        this(redisTemplate, lockKey);
        log.debug("RedisLock(timeOut: {})", timeOut);
        this.setTimeOut(timeOut);
    }

    /**
     * 使用默认的锁的过期时间，指定请求锁的超时时间
     * @param redisTemplate
     * redisTemplate
     * @param lockKey
     * 锁的key(Redis的Key)
     * @param expireTime
     * 锁的过期时间(单位：秒)
     * @param timeOut
     * 请求锁的超时时间(单位：毫秒)
     */
    public RedisLock(@Nonnull final RedisTemplate<Object, Object> redisTemplate,@Nonnull final String lockKey, int expireTime, long timeOut){
        this(redisTemplate, lockKey);
        log.debug("RedisLock(expireTime: {}, timeOut: {})..", expireTime, timeOut);
        this.setExpireTime(expireTime);
        this.setTimeOut(timeOut);
    }

    /**
     * 尝试获取锁 超时返回
     * @return 返回获取锁的结果
     */
    public boolean tryLock(){
        log.debug("tryLock...");
        //生成随机key值
        this.lockValue = UUID.randomUUID().toString();
        //请求锁超时时间,纳秒
        final long timeout = timeOut * 1000000;
        //系统当前时间,纳秒
        final long nowTime = System.nanoTime();
        while ((System.nanoTime() - nowTime) < timeout){
            if(set(lockKey, lockValue, expireTime)){
                locked.set(true);
                //上锁成功，结束请求
                return true;
            }
            //每次请求等待一段时间
            sleep();
        }
        return locked.get();
    }

    /**
     * 获取锁
     * @return 获取锁结果
     */
    public boolean lock(){
        log.debug("lock...");
        //生成随机key值
        this.lockValue = UUID.randomUUID().toString();
        //获取锁
        if(set(lockKey, lockValue, expireTime)){
            locked.set(true);
            //上锁成功
            return true;
        }
        return false;
    }

    /**
     * 以阻塞方式的获取锁
     * @return 是否成功获得锁
     */
    public boolean lockBlock(){
        log.debug("lockBlock...");
        //生成随机key值
        this.lockValue = UUID.randomUUID().toString();
        while (true){
            if(set(lockKey, lockValue, expireTime)){
                locked.set(true);
                //上锁成功，结束请求
                return true;
            }
            //每次请求等待一段时间
            sleep();
        }
    }

    /**
     * 解锁
     * <p>
     *     可以通过以下修改，让这个锁实现更健壮：
     * </p>
     * <p>
     *     不使用固定的字符串作为键的值，而是设置一个不可猜测（non-guessable）的长随机字符串，作为口令串（token）。
     *     不使用 DEL 命令来释放锁，而是发送一个 Lua 脚本，这个脚本只在客户端传入的值和键的口令串相匹配时，才对键进行删除
     *     这两个改动可以防止持有过期锁的客户端误删现有锁的情况出现。
     * </p>
     * @return 解锁结果
     */
    public Boolean unlock(){
        log.debug("unlock...");
        //只有加锁成功并且锁还有效才去释放锁
        if(locked.get()){
            final byte[] lua = UNLOCK_LUA.getBytes(Constants.CHARSET),
                         key = lockKey.getBytes(Constants.CHARSET),
                         value = lockValue.getBytes(Constants.CHARSET);
            return redisTemplate.execute((RedisCallback<Boolean>)connection-> connection.eval(lua, ReturnType.BOOLEAN, 1, key, value));
        }
        return true;
    }

    /**
     * 重写redisTemplate的set方法
     * <p>
     *     命令 SET resource-name anystring NX EX max-lock-time 是一种在 Redis 中实现锁的简单方法。
     * </p>
     * <p>
     *     客户端执行以上的命令：
     * </p>
     * <p>
     *     如果服务器返回 OK ，那么这个客户端获得锁。
     *     如果服务器返回 NIL ，那么客户端获取锁失败，可以在稍后再重试。
     * </p>
     * @param key
     * 锁的Key
     * @param value
     * 锁的值
     * @param seconds
     * 过期时间(秒)
     * @return 写入结果
     */
    private Boolean set(final String key, final String value, final long seconds){
        log.debug("set(key: {}, value: {}, seconds: {})...", key, value, seconds);
        //检查参数
        Assert.hasText(key, "'key'不能为空!");
        //
        final byte[] k = key.getBytes(Constants.CHARSET), val = value.getBytes(Constants.CHARSET);
        //
        return redisTemplate.execute(
                (RedisCallback<Boolean>) connection -> connection.set(k, val,
                        Expiration.from(seconds, TimeUnit.SECONDS),
                        RedisStringCommands.SetOption.SET_IF_ABSENT)
        );
    }

    /**
     * 线程等待时间
     */
    private void sleep(){
        try{
            final long millis = 10;
            final int nanos = 50000;
            Thread.sleep(millis, RANDOM.nextInt(nanos));
        }catch(InterruptedException e){
            log.warn("sleep-获取分布式锁休眠被中断:" + e.getMessage(), e);
        }
    }
}
