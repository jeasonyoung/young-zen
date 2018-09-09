package org.young.wechat.message.callback;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 回复图文消息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NewsCallbackMessage extends BaseCallbackMessage {
    /**
     * 图文消息个数，限制为8条以内
     */
    @XStreamAlias("ArticleCount")
    private Integer articleCount;
    /**
     * 多条图文消息信息，默认第一个item为大图,注意，如果图文数超过8，则将会无响应
     */
    @XStreamAlias("Articles")
    private List<ArticleItem> articles;

    /**
     * 构造函数。
     */
    public NewsCallbackMessage() {
        super("news");
    }

    /**
     * 图文消息项
     */
    @Data
    @XStreamAlias("item")
    public class ArticleItem implements Serializable {
        /**
         * 图文消息标题
         */
        @XStreamAlias("Title")
        private String title;
        /**
         * 图文消息描述
         */
        @XStreamAlias("Description")
        private String description;
        /**
         * 图片链接，支持JPG、PNG格式，较好的效果为大图360*200，小图200*200
         */
        @XStreamAlias("PicUrl")
        private String picUrl;
        /**
         * 点击图文消息跳转链接
         */
        @XStreamAlias("Url")
        private String url;
    }
}