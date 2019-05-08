package org.young.common.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 缓存工具类
 *
 * @author yangyong
 * @version 1.0
 **/
@Slf4j
public class CacheUtils {

    /**
     * 创建缓存器
     * @param <K>
     *     缓存键
     * @param <V>
     *     缓存值
     * @return 缓存器
     */
    public static <K, V> Cache<K, V> createCache(){
        return createCache(500, 30, TimeUnit.SECONDS);
    }

    /**
     * 创建缓存器
     * @param maxSize
     * 最大缓存量
     * @param duration
     * 缓存期
     * @param timeUnit
     * 时间单位
     * @param <K>
     *     缓存键
     * @param <V>
     *     缓存值
     * @return 缓存器
     */
    public static <K, V> Cache<K, V> createCache(final int maxSize, final int duration, final TimeUnit timeUnit){
        log.debug("createCache(maxSize: {}, duration: {}, timeUnit: {})...", maxSize, duration, timeUnit);
        return CacheBuilder.newBuilder()
                .maximumSize(maxSize > 0 ? maxSize : 100)
                .expireAfterWrite(duration < 0 ? 2 : duration, timeUnit == null ? TimeUnit.HOURS : timeUnit)
                .build();
    }

    /**
     * 获取缓存值
     * @param cache
     * 缓存器
     * @param key
     * 缓存键
     * @param loader
     * 缓存加载器
     * @param <K>
     *     缓存键
     * @param <V>
     *     缓存值
     * @return 缓存值
     */
    public static <K, V> V getCacheValue(@Nonnull final Cache<K, V> cache, @Nonnull final K key, @Nonnull Callable<? extends V> loader){
        try{
            final V data = cache.get(key, loader);
            if(data == null){
                cache.invalidate(key);
            }
            return data;
        }catch (Throwable e){
            log.warn("getCacheValue(key: {})-exp: {}", key, e.getMessage());
        }
        return null;
    }
}
