package org.young.common.lock;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 缓存键生成器接口
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/9/10 18:01
 */
public interface LockKeyGenerator {

    /**
     * 获取AOP参数,生成指定的缓存key
     * @param joinPoint
     * AOP参数
     * @return 缓存key
     */
    String getLockKey(final ProceedingJoinPoint joinPoint);
}
