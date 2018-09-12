package org.young.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * 验证码配置属性
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/8/28 16:27
 */
@Data
@ConfigurationProperties(prefix = "young.auth.verify-code")
public class VerifyCodeProperties implements Serializable {
    /**
     * 长度
     */
    private Integer length = 6;
    /**
     * 有效期
     */
    private Integer duration = 120;
}
