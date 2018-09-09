package org.young.wechat.message.callback;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.young.wechat.message.MessageType;

/**
 * 回复文本消息
 * @author jeasonyoung
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TextCallbackMessage extends BaseCallbackMessage {
    /**
     * 内容
     */
    @XStreamAlias("Content")
    private String content;
    /**
     * 构造函数。
     */
    public TextCallbackMessage(){
        super(MessageType.TEXT.getName());
    }
}
