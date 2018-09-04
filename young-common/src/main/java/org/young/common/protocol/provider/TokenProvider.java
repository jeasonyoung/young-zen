package org.young.common.protocol.provider;

import org.young.common.exception.TokenException;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * 令牌数据接口
 * @author jeasonyoung
 */
public interface TokenProvider extends Serializable {

    /**
     * 根据渠道令牌加载用户数据。
     * @param channel
     * 渠道号。
     * @param token
     * 登录令牌。
     * @return 用户数据。
     * @throws TokenException
     * 令牌异常。
     */
    TokenUser loadUserByToken(@Nonnull final Integer channel, @Nonnull final String token) throws TokenException;
}
