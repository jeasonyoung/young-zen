package org.young.wechat.message.receives;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 语音消息
 * @author jeasonyoung
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VoiceMessage extends BaseNormalMessage {
    /**
     * 语音消息媒体id
     */
    @XStreamAlias("MediaId")
    private String mediaId;
    /**
     * 语音格式
     */
    @XStreamAlias("Format")
    private String format;
    /**
     * 语音识别结果
     */
    @XStreamAlias("Recognition")
    private String recognition;
}
