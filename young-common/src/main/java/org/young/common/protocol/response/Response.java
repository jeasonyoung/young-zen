package org.young.common.protocol.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 报文协议-响应报文
 * @param <T>
 *     响应报文体
 * @author jeasonyoung
 */
@Data
public class Response<T extends Serializable> implements Serializable {
    /**
     * 响应报文头
     */
    private RespHead head;
    /**
     * 响应报文体
     */
    private T body;
}
