package org.young.common.protocol.provider;

import lombok.Data;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * 令牌用户数据
 * @author jeasonyoung
 */
@Data
public class TokenUser implements Serializable {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 登录ID
     */
    private String loginId;

    /**
     * 创建令牌用户数据
     * @param loginId
     * 登录ID
     * @param userId
     * 用户ID
     * @return 令牌用户数据
     */
    public static TokenUser create(@Nonnull final String loginId,@Nonnull final String userId){
        final TokenUser data = new TokenUser();
        //设置登录ID
        data.setLoginId(loginId);
        //设置用户ID
        data.setUserId(userId);
        return data;
    }
}
