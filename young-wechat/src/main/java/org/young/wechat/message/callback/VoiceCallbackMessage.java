package org.young.wechat.message.callback;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.young.wechat.message.MessageType;

/**
 * 回复语音消息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VoiceCallbackMessage extends BaseCallbackMessage {

    /**
     * 通过素材管理中的接口上传多媒体文件，得到的id
     */
    @XStreamAlias("MediaId")
    private String mediaId;

    /**
     * 构造函数。
     */
    public VoiceCallbackMessage() {
        super(MessageType.VOICE.getName());
    }
}
