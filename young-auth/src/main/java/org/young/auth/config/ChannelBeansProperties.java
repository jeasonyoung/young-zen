package org.young.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * 渠道处理器-配置属性
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/26 10:19
 */
@Data
@ConfigurationProperties(prefix = "young.auth.channel-beans")
public class ChannelBeansProperties {
    /**
     * 认证配置
     */
    private Map<Integer, List<String>> authen;
}
