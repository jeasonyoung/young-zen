package org.young.common.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;

/**
 * Cache 数据缓存存储到 Redis中配置
 * @author jeasonyoung
 */
@Slf4j
public class RedisCacheConfigurer extends CachingConfigurerSupport {

    @Bean
    @Primary
    @Override
    public KeyGenerator keyGenerator() {
        log.debug("keyGenerator...");
        return (target, method, params) -> {
            //Cacheable
            final Cacheable cacheable = method.getAnnotation(Cacheable.class);
            if(cacheable != null){
                return createKey(cacheable.cacheNames(), target, method, params);
            }
            //CacheEvict
            final CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);
            if(cacheEvict != null){
                return createKey(cacheEvict.cacheNames(), target, method, params);
            }
            //CachePut
            final CachePut cachePut = method.getAnnotation(CachePut.class);
            if(cachePut != null){
                return createKey(cachePut.cacheNames(), target, method, params);
            }
            //创建默认缓存键
            return createDefaultKey(target, method, params);
        };
    }

    /**
     * 创建Redis模板
     * @param redisConnectionFactory
     * Redis连接工厂。
     * @return redis模板
     */
    @Primary
    @Bean(name = "redisTemplate")
    public RedisTemplate<Object, Object> createRedisTemplate(final RedisConnectionFactory redisConnectionFactory){
        log.debug("createRedisTemplate(redisConnectionFactory: {})...", redisConnectionFactory);
        final FastJsonRedisTemplate redisTemplate = new FastJsonRedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    /**
     * 重置缓存管理器。
     * @param redisConnectionFactory
     * Redis连接工厂。
     * @return 缓存管理器。
     */
    @Bean
    public CacheManager createCacheManager(final RedisConnectionFactory redisConnectionFactory){
        log.debug("createCacheManager(redisConnectionFactory: {})...", redisConnectionFactory);
        //
        final GenericFastJsonRedisSerializer fastJsonRedisSerializer = new GenericFastJsonRedisSerializer();
        final RedisSerializationContext redisSerializationContext = RedisSerializationContext.fromSerializer(fastJsonRedisSerializer);
        final RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(redisSerializationContext.getValueSerializationPair())
                //.disableCachingNullValues()
                .entryTtl(Duration.ofMinutes(30));
        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }

    private static String createKey(@Nullable final String[] cachemes, @Nonnull Object target,@Nonnull Method method,@Nullable Object... params){
        String key = null;
        if(cachemes != null && cachemes.length > 0){
            key = Joiner.on("-").join(cachemes);
        }
        if(!Strings.isNullOrEmpty(key)){
            return key;
        }
        return createDefaultKey(target, method, params);
    }

    private static String createDefaultKey(@Nonnull final Object target,@Nonnull final Method method,@Nullable final Object... params){
        //格式化缓存key字符串
        return (target.getClass().getSimpleName() + "." + method.getName() + Arrays.toString(params)).toLowerCase();
    }
}
