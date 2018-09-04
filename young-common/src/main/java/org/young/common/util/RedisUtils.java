package org.young.common.util;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * redis工具类
 *
 * @author yangyong young1982@foxmail.com
 * @date 2018/8/30 19:21
 */
@Slf4j
public class RedisUtils {
    private static final int REDIS_TIMEOUT = 86400;
    private static final int REDIS_READ_TIMEOUT = 10;

    /**
     * 清空Redis缓存键
     * @param redisTemplate
     * Redis操作模板
     * @param key
     * 缓存键
     */
    public static void clearRedisKey(@Nonnull final RedisTemplate<Object, Object> redisTemplate, @Nonnull final String key){
        log.debug("clearRedisKey(redisTemplate: {}, key: {})...", redisTemplate, key);
        //检查参数
        Assert.hasText(key, "'key'不能为空!");
        try {
            //清空缓存数据
            final Boolean ret = redisTemplate.delete(key);
            log.debug("clearRedisKey-delete(key: {})-ret: {}", key, ret);
        }catch (Throwable ex){
            log.error("clearRedisKey(key: "+ key +")-exp:" + ex.getMessage(), ex);
        }
    }

    /**
     * 写入Redis缓存
     * @param redisTemplate
     * Redis操作模板
     * @param key
     * 缓存键
     * @param data
     * 缓存值
     * @param <T>
     *     缓存类型
     */
    public static <T extends Serializable> void writeRedisCache(@Nonnull final RedisTemplate<Object, Object> redisTemplate, @Nonnull final String key, @Nonnull final T data){
        log.debug("writeRedisCache(redisTemplate: {}, key: {}, data: {})...", redisTemplate, key, data);
        //检查参数
        Assert.hasText(key, "'key'不能为空!");
        try{
            //缓存数据json化处理
            final String json = toJson(data);
            if(!Strings.isNullOrEmpty(json)){
                //缓存数据
                redisTemplate.opsForValue().set(key, json, REDIS_TIMEOUT, TimeUnit.SECONDS);
            }
            log.info("writeRedisCache(key: {})=> {}", key, json);
        }catch (Throwable ex){
            log.error("writeRedisCache(key: "+ key +", data: "+ data +")-exp:" + ex.getMessage(), ex);
        }
    }

    /**
     * 读取Redis缓存数据
     * @param redisTemplate
     * Redis操作模板
     * @param key
     * 缓存键
     * @param clazz
     * 缓存数据类型
     * @param <T>
     *     缓存数据类型
     * @return 缓存数据
     */
    public static <T extends Serializable> T readRedisCache(@Nonnull final RedisTemplate<?, ?> redisTemplate, @Nonnull final String key, @Nonnull Class<T> clazz){
        log.debug("readRedisCache(redisTemplate: {}, key: {}, clazz: {})...", redisTemplate, key, clazz);
        //检查参数
        Assert.hasText(key, "'key'不能为空!");
        try{
            //读取缓存数据
            final Object obj = redisTemplate.opsForValue().get(key);
            if(obj != null){
                return parseObject(obj, clazz);
            }
        }catch (Throwable ex){
            log.error("readRedisCache(key: "+ key +", clazz: "+ clazz +")-exp:" + ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * 写入缓存列表数据
     * @param redisTemplate
     * Redis操作模板
     * @param key
     * 缓存键
     * @param data
     * 缓存数据
     */
    public static <T extends Serializable> void writeRedisCacheList(@Nonnull final RedisTemplate<Object, Object> redisTemplate, @Nonnull final String key, @Nonnull final T data){
        log.debug("addCacheList(key: {}, data: {})...", key, data);
        try {
            final String json = toJson(data);
            Long ret = null;
            if (!Strings.isNullOrEmpty(json)) {
                ret = redisTemplate.opsForList().rightPush(key, json);
            }
            log.info("addCacheList(key: {}, json: {})-ret: {}", key, json, ret);
        }catch (Throwable ex){
            log.error("addCacheList(key: "+ key +", data: "+ data +")-exp:" + ex.getMessage(), ex);
        }
    }

    /**
     * 读取全部缓存数据
     * @param redisTemplate
     * Redis操作模板
     * @param key
     * 缓存键
     * @return 缓存数据集合
     */
    public static <T extends Serializable> List<T> readRedisCacheListAll(@Nonnull final RedisTemplate<Object, Object> redisTemplate, @Nonnull final String key, @Nonnull final Class<T> clazz){
        log.debug("readRedisCacheListAll(redisTemplate: {}, key: {}, clazz: {})...", redisTemplate, key, clazz);
        try {
            final Long size = redisTemplate.opsForList().size(key);
            if (size != null && size > 0) {
                //取出全部数据
                return ConvertUtils.convertHandler(
                        redisTemplate.opsForList().range(key, 0, size),
                        item -> parseObject(item, clazz)
                );
            }
        }catch (Throwable ex){
            log.error("readRedisCacheListAll(redisTemplate: "+ redisTemplate +", key: "+ key +", clazz: "+ clazz +")-exp:" + ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * 逐个读取缓存数据处理
     * @param redisTemplate
     * Redis操作模板
     * @param key
     * 缓存键
     * @param clazz
     * 缓存数据类型
     * @param handler
     * 逐个缓存数据处理
     * @param <T>
     *     缓存数据类型
     * @return 执行的数据量
     */
    public static <T extends Serializable> long readRedisCacheListPop(
            @Nonnull final RedisTemplate<Object, Object> redisTemplate,
            @Nonnull final String key,
            @Nonnull final Class<T> clazz,
            @Nonnull final ReadCacheHandler<T> handler){
        log.debug("readRedisCacheListPop(redisTemplate: {}, key: {}, clazz: {}, handler: {})...", redisTemplate, key, clazz, handler);
        long index = 0;
        try {
            final Long size = redisTemplate.opsForList().size(key);
            if(size != null && size > 0) {
                final int totals = size.intValue();
                Object obj;
                while ((obj = redisTemplate.opsForList().leftPop(key, REDIS_READ_TIMEOUT, TimeUnit.SECONDS)) != null){
                    try{
                        //当前行索引
                        index++;
                        //当前行数据处理
                        handler.hander(index, totals, parseObject(obj, clazz));
                    }catch (Throwable e){
                        log.warn("readRedisCacheListPop(key: "+ key +", index: "+ index +", totals: "+ totals +")-exp:" + e.getMessage(), e);
                    }
                }
            }
        }catch (Throwable ex){
            log.error("readRedisCacheListPop(redisTemplate: "+ redisTemplate +", key: "+ key +", clazz: "+ clazz +", handler: "+ handler +")-exp:"+ ex.getMessage(), ex);
        }
        return index;
    }


    /**
     * 对象转换为JSON
     * @param data
     * 对象数据
     * @param <T>
     *     对象类型
     * @return JSON
     */
    private static <T extends Serializable> String toJson(@Nonnull final T data){
        return JSON.toJSONString(data);
    }

    /**
     * 数据转换
     * @param obj
     * 源对象
     * @param clazz
     * 目标数据类型
     * @param <T>
     *     目标数据泛型
     * @return 目标数据
     */
    private static <T extends Serializable> T parseObject(@NonNull final Object obj, @NonNull final Class<T> clazz){
        log.debug("parseObject(obj: {}, clazz: {})...", obj, clazz);
        if(obj instanceof String){
            final String json = (String) obj;
            if (!Strings.isNullOrEmpty(json)) {
                try {
                    return JSON.parseObject(json, clazz);
                } catch (Throwable ex) {
                    log.error("parseObject(json: " + json + ")-exp:" + ex.getMessage(), ex);
                }
            }
        }
        return null;
    }

    /**
     * 读取缓存数据处理
     * @param <T>
     *     数据类型
     */
    public interface ReadCacheHandler<T extends Serializable> {

        /**
         * 数据处理
         * @param index
         * 当前索引
         * @param totals
         * 总数据
         * @param data
         * 目标数据
         */
        void hander(final long index, final long totals, @NonNull final T data);
    }
}
