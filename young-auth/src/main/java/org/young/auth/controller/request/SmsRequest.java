package org.young.auth.controller.request;

import lombok.Data;
import org.young.common.protocol.request.ReqIgnoreTokenVerify;
import org.young.common.protocol.request.Request;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 验证短信-请求报文
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/9/12 09:39
 */
public class SmsRequest extends Request<SmsRequest.ReqSmsBody> implements ReqIgnoreTokenVerify {
    /**
     * 验证短信-请求报文体
     */
    @Data
    public static class ReqSmsBody implements Serializable {
        /**
         * 手机号码
         */
        @Pattern(regexp = "1[3|4|5|7|8][0-9]\\d{8}", message = "手机号码不正确")
        private String mobile;
    }
}
