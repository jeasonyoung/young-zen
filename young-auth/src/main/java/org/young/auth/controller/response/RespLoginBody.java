package org.young.auth.controller.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.young.auth.model.UserInfo;
import org.young.common.protocol.response.BaseRespBody;

/**
 * 登录-响应报文体
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/9/12 09:34
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RespLoginBody extends BaseRespBody {
    /**
     * 登录令牌(过期时间: 7200s)
     */
    private String token;
    /**
     * 刷新令牌(令牌过期之后使用刷新令牌获取新的令牌)
     */
    private String refreshToken;
    /**
     * 用户信息
     */
    private UserInfo user;
}
