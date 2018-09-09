package org.young.wechat.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

import java.io.Serializable;

/**
 * 微信消息基类。
 * @author jeasonyoung
 */
@Data
@XStreamAlias(BaseMessage.ROOT)
public class BaseMessage implements Serializable {
    public static final String ROOT = "xml";

    /**
     * 收到的OpenID
     */
    @XStreamAlias("ToUserName")
    private String toUserName;
    /**
     * 开发者微信号
     */
    @XStreamAlias("FromUserName")
    private String fromUserName;
    /**
     * 消息创建时间(整型)
     */
    @XStreamAlias("CreateTime")
    private Long createTime;
    /**
     * 消息类型
     */
    @XStreamAlias("MsgType")
    private String msgType;
}
