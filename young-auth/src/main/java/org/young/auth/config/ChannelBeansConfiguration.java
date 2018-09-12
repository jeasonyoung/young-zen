package org.young.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 渠道处理器-配置
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/8/24 21:16
 */
@Configuration
@EnableConfigurationProperties(ChannelBeansProperties.class)
public class ChannelBeansConfiguration {

}
