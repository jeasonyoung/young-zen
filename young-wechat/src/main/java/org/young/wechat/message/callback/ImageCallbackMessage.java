package org.young.wechat.message.callback;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import org.young.wechat.message.MessageType;

/**
 * 回复图片消息
 * @author jeasonyoung
 */
@Data
public class ImageCallbackMessage extends BaseCallbackMessage {

    /**
     * 通过素材管理中的接口上传多媒体文件，得到的id
     */
    @XStreamAlias("MediaId")
    private String mediaId;

    /**
     * 构造函数。
     */
    public ImageCallbackMessage() {
        super(MessageType.IMAGE.getName());
    }
}
