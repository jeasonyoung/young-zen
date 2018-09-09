package org.young.wechat.message.receives;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.young.wechat.message.BaseMessage;

/**
 * 普通消息。
 * @author jeasonyoung
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseNormalMessage extends BaseMessage {
    /**
     * 消息id
     */
    @XStreamAlias("MsgId")
    private Long msgId;
}
