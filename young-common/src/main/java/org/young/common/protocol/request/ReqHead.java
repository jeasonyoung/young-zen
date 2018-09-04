package org.young.common.protocol.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 报文协议-请求报文头
 * @author jeasonyoung
 */
@Data
public class ReqHead implements Serializable {
    /**
     * 版本号
     */
    private Integer version;
    /**
     * 渠道号
     */
    private Integer channel;
    /**
     * 用户令牌
     */
    private String token;
    /**
     * 提交时间戳
     */
    private long time;
    /**
     * 签名戳
     */
    private String sign;
}
