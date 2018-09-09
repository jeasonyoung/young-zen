package org.young.wechat.message.callback;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.young.wechat.message.BaseMessage;

/**
 * 回复消息基类。
 * @author jeasonyoung.
 */
@XStreamAlias(BaseCallbackMessage.ROOT)
public abstract class BaseCallbackMessage extends BaseMessage {

    /**
     * 构造函数。
     * @param msgType
     * 消息类型
     */
    BaseCallbackMessage(final String msgType){
        this.setMsgType(msgType);
        this.setCreateTime(System.currentTimeMillis() / 1000);
    }
}
