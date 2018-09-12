package org.young.auth.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.young.common.config.RedisCacheConfigurer;

/**
 * redis缓存配置
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/17 10:38
 */
@Configuration
@EnableCaching
public class RedisCacheConfigration extends RedisCacheConfigurer {

}
