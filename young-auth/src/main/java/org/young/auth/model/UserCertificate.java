package org.young.auth.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户认证数据。
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/26 14:38
 */
@Data
public class UserCertificate implements Serializable {
    /**
     * 登录令牌(过期时间: 7200s)
     */
    private String token;
    /**
     * 刷新令牌(令牌过期之后使用刷新令牌获取新的令牌)
     */
    private String refreshToken;
    /**
     * 用户信息。
     */
    private UserInfo user;
}
