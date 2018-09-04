package org.young.common.protocol;

import java.nio.charset.Charset;

/**
 * 报文-协议常量
 * @author jeasonyoung
 */
public class Constants {

    /**
     * 编码格式。
     */
    public static final Charset CHARSET = Charset.forName("UTF-8");
    /**
     * 消息内容格式
     */
    public static final String CONTENT_TYPE_WITH_JSON = "application/json;charset=" + CHARSET.name();
    /**
     * 消息头字段
     */
    public static final String HEAD = "head";
    /**
     * 消息体字段
     */
    public static final String BODY = "body";
    /**
     * 消息头签名字段
     */
    public static final String REQ_HEAD_BY_SIGN_KEY = "sign";
}
