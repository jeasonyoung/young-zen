package org.young.auth.controller.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.young.common.protocol.response.BaseRespBody;

/**
 * 验证短信-响应报文体
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/9/12 10:16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RespSmsBody extends BaseRespBody {
    /**
     * 验证码ID。
     */
    private String valid;
}
