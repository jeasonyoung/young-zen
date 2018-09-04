package org.young.common.protocol.provider;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 令牌用户数据。
 *
 * @author yangyong young1982@foxmail.com
 * @date 2018/7/31 10:22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TokenUserData extends TokenUser {
    /**
     * 令牌
     */
    private String token;
    /**
     * 刷新令牌
     */
    private String refreshToken;
}
