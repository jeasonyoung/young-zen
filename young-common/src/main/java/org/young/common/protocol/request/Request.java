package org.young.common.protocol.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 报文协议-请求报文
 * @param <T>
 *     报文体类型
 * @author jeasonyoung
 */
@Data
public class Request<T extends Serializable> implements Serializable {
    /**
     * 报文头
     */
    private ReqHead head;
    /**
     * 报文体
     */
    private T body;
}
