package org.young.common.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 锁注解
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/9/10 17:49
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Lock {

    /**
     * 锁key的前缀
     * @return 锁key的前缀
     */
    String prefix() default "lock_";

    /**
     * 过期时间(默认 5)
     * @return 过期时间
     */
    int expire() default 5;

    /**
     * 过期时间单位(默认 秒)
     * @return 过期时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 锁key分隔符(默认 :)
     * @return 锁key分隔符
     */
    String delimiter() default ":";
}
