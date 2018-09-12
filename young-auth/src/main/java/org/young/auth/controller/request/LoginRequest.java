package org.young.auth.controller.request;

import lombok.Data;
import org.young.common.protocol.request.ReqIgnoreTokenVerify;
import org.young.common.protocol.request.Request;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 用户登录-请求报文
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/9/12 09:22
 */
public class LoginRequest extends Request<LoginRequest.ReqLoginBody> implements ReqIgnoreTokenVerify {

    /**
     * 登录-请求报文体
     */
    @Data
    public static class ReqLoginBody implements Serializable {
        /**
         * 登录账号
         */
        @NotEmpty(message = "登录账号不能为空!")
        private String account;
        /**
         * 密码
         */
        private String password;
        /**
         * 设备标识
         */
        private String mac;
        /**
         * 验证码标识
         */
        private String valid;
        /**
         * 验证码值
         */
        private String validCode;
    }
}
