package org.young.common.protocol.provider;

import lombok.Data;

import java.io.Serializable;

/**
 * 令牌用户-数据接口.
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
}
