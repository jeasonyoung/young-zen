package org.young.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Redis session
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/17 21:32
 */
@Configuration
@EnableRedisHttpSession
public class RedisSessionConfiguration {

}
