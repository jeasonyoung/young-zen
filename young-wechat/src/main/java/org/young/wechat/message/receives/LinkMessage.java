package org.young.wechat.message.receives;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 链接消息
 * @author jeasonyoung
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LinkMessage extends BaseNormalMessage {
    /**
     * 消息标题
     */
    @XStreamAlias("Title")
    private String title;
    /**
     * 消息描述
     */
    @XStreamAlias("Description")
    private String description;
    /**
     * 消息链接
     */
    @XStreamAlias("Url")
    private String url;
}
