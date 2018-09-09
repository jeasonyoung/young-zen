package org.young.wechat.message.receives;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图片消息。
 * @author jeasonyoung
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ImageMessage extends BaseNormalMessage {
    /**
     * 图片链接
     */
    @XStreamAlias("PicUrl")
    private String picUrl;
    /**
     * 图片消息媒体id
     */
    @XStreamAlias("MediaId")
    private String mediaId;
}
