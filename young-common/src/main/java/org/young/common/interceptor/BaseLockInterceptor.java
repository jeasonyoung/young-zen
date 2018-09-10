package org.young.common.interceptor;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.young.common.lock.Lock;
import org.young.common.lock.LockKeyGenerator;
import org.young.common.lock.LockKeyGeneratorDefaultImpl;
import org.young.common.lock.RedisLock;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁Aop
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/9/10 21:24
 */
@Slf4j
public abstract class BaseLockInterceptor {
    private final LockKeyGenerator keyGenerator = new LockKeyGeneratorDefaultImpl();

    /**
     * 注入-Redis template
     */
    @Autowired(required = false)
    private RedisTemplate<Object, Object> redisTemplate = null;

    /**
     * aop拦截器
     * @param joinPoint
     * aop参数
     * @return 执行结果
     */
    @Around("execution(public * *(..)) && @annotation(org.young.common.lock.Lock)")
    public Object interceptor(final ProceedingJoinPoint joinPoint){
        log.debug("interceptor(joinPoint: {})...", joinPoint);
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final Method method = signature.getMethod();
        //
        final Lock annotation = method.getAnnotation(Lock.class);
        if(Strings.isNullOrEmpty(annotation.prefix())){
            log.error("interceptor-Lock: 未配置锁前缀!");
            throw new RuntimeException("lock key don't null");
        }
        final String lockKey = keyGenerator.getLockKey(joinPoint);
        log.info("interceptor-getLockKey-lockKey: {}", lockKey);
        //
        final int expire = (int) TimeUnit.SECONDS.convert(annotation.expire(), annotation.timeUnit());
        final RedisLock redisLock = redisTemplate == null ? null : new RedisLock(redisTemplate, lockKey, expire);
        try{
            if(redisLock != null) {
                final boolean success = redisLock.tryLock();
                log.info("interceptor-tryLock: {}", success);
                if (!success) {
                    log.warn("interceptor-tryLock: 没有获得锁!");
                    throw new RuntimeException("请勿重复执行!");
                }
            }
            //执行原程序
            return joinPoint.proceed();
        }catch (Throwable ex){
            log.error("interceptor-exp:" + ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }finally {
            if(redisLock != null) {
                //解锁
                final boolean success = redisLock.unlock();
                log.info("interceptor-unlock-success: {}", success);
            }
        }
    }

}
