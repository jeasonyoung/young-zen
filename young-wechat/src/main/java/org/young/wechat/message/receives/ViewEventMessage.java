package org.young.wechat.message.receives;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 点击菜单跳转链接时的事件推送
 * @author jeasonyoung
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ViewEventMessage extends BaseEventMessage {
    /**
     * 事件KEY值，与自定义菜单接口中KEY值对应
     */
    @XStreamAlias("EventKey")
    private String eventKey;
    /**
     * 菜单ID。
     */
    @XStreamAlias("MenuId")
    private String menuId;
}
