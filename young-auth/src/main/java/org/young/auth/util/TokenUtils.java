package org.young.auth.util;

import org.young.common.util.EncryptUtils;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * 令牌工具类。
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/31 11:00
 */
public class TokenUtils {

    /**
     * 创建新令牌。
     * @return 新令牌。
     */
    public static String createNewToken() {
        final String newId = UUID.randomUUID().toString();
        return EncryptUtils.createSignatureEncrypt(EncryptUtils.createSignatureEncrypt(newId) + System.currentTimeMillis());
    }

    /**
     * 创建刷新令牌。
     * @param loginId
     * 用户登录ID。
     * @param token
     * 登录令牌。
     * @return 刷新令牌。
     */
    public static String createNewRefreshToken(@Nonnull final String loginId, @Nonnull final String token){
        return EncryptUtils.createSignatureEncrypt(EncryptUtils.createSignatureEncrypt(loginId) + token);
    }
}
