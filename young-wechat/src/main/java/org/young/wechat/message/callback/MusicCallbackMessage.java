package org.young.wechat.message.callback;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 回复音乐消息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MusicCallbackMessage extends BaseCallbackMessage {

    /**
     * 音乐标题
     */
    @XStreamAlias("Title")
    private String title;
    /**
     * 音乐描述
     */
    @XStreamAlias("Description")
    private String description;
    /**
     * 音乐链接
     */
    @XStreamAlias("MusicURL")
    private String musicURL;
    /**
     * 高质量音乐链接，WIFI环境优先使用该链接播放音乐
     */
    @XStreamAlias("HQMusicUrl")
    private String hqMusicUrl;
    /**
     * 缩略图的媒体id，通过素材管理中的接口上传多媒体文件得到的id
     */
    @XStreamAlias("ThumbMediaId")
    private String thumbMediaId;

    /**
     * 构造函数。
     */
    public MusicCallbackMessage() {
        super("music");
    }
}
