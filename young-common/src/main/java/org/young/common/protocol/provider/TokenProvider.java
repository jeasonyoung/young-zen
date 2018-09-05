package org.young.common.protocol.provider;

import org.young.common.Callback;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * 令牌数据接口
 * @author jeasonyoung
 */
public interface TokenProvider extends Serializable {

    /**
     * 加载渠道令牌下数据
     * @param channel
     * 渠道号
     * @param token
     * 用户令牌
     * @return 用户数据
     */
    Callback<TokenUser> loadUserByToken(@Nonnull final Integer channel, @Nonnull final String token);
}
