package org.young.wechat.message.receives;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文本消息。
 * @author jeasonyoung
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TextMessage extends BaseNormalMessage {
    /**
     * 文本消息内容
     */
    @XStreamAlias("Content")
    private String content;
}
