package org.young.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 验证码配置
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/8/28 16:31
 */
@Configuration
@EnableConfigurationProperties(VerifyCodeProperties.class)
public class VerifyCodeConfiguration {

}
