package org.young.common.protocol.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 报文协议-响应报文头
 * @author jeasonyoung
 */
@Data
public class RespHead implements Serializable {
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 消息内容
     */
    private String msg;
    /**
     * 版本更新
     */
    private RespVersion version;
}
