package org.young.wechat.message.receives;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 小视频消息
 * @author jeasonyoung
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ShortVideoMessage extends BaseNormalMessage {
    /**
     * 视频消息媒体id
     */
    @XStreamAlias("MediaId")
    private String mediaId;
    /**
     * 视频消息缩略图的媒体id
     */
    @XStreamAlias("ThumbMediaId")
    private String thumbMediaId;
}
