package org.young.wechat.message.receives;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.young.wechat.message.BaseMessage;

/**
 * 事件消息。
 * @author jeasonyoung
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseEventMessage extends BaseMessage {
    /**
     * 事件
     */
    @XStreamAlias("Event")
    private String event;
}
