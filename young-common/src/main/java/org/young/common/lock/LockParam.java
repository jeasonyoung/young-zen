package org.young.common.lock;

import java.lang.annotation.*;

/**
 * 锁key的参数
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/9/10 17:57
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LockParam {
    /**
     * 字段名称(支持spel表达式)
     * @return 字段名称
     */
    String name() default "";
}
