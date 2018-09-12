package org.young.auth.controller.request;

import lombok.Data;
import org.young.common.protocol.request.ReqIgnoreTokenVerify;
import org.young.common.protocol.request.Request;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 刷新令牌-请求报文
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/9/12 11:40
 */
public class RefreshRequest extends Request<RefreshRequest.ReqRefreshBody> implements ReqIgnoreTokenVerify {

    /**
     * 刷新令牌-请求报文体
     */
    @Data
    public static class ReqRefreshBody implements Serializable {
        /**
         * 刷新令牌
         */
        @NotBlank(message = "刷新令牌为空!")
        private String refreshToken;
    }
}
