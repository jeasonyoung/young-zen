package org.young.wechat.message.callback;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.young.wechat.message.MessageType;

/**
 * 回复视频消息
 * @author jeasonyoung
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VideoCallbackMessage extends BaseCallbackMessage {
    /**
     * 通过素材管理中的接口上传多媒体文件，得到的id
     */
    @XStreamAlias("MediaId")
    private String mediaId;
    /**
     * 视频消息的标题
     */
    @XStreamAlias("Title")
    private String title;
    /**
     * 视频消息的描述
     */
    @XStreamAlias("Description")
    private String description;

    /**
     * 构造函数。
     */
    public VideoCallbackMessage() {
        super(MessageType.VIDEO.getName());
    }
}
