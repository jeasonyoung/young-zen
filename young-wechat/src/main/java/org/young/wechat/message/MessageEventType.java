package org.young.wechat.message;

import com.google.common.base.Strings;
import lombok.Getter;

import javax.annotation.Nonnull;

/**
 * 消息事件类型
 * @author jeasonyoung
 */
@Getter
public enum MessageEventType {
    /**
     * 用户未关注时关注
     */
    SUBSCRIBE("subscribe","用户未关注时关注"),
    /**
     * 取消订阅
     */
    UNSUBSCRIBE("unsubscribe","取消订阅"),
    /**
     * 用户已关注时
     */
    SCAN("scan","用户已关注时"),
    /**
     * 上报地理位置
     */
    LOCATION("location","上报地理位置"),
    /**
     * 点击菜单消息
     */
    CLICK("click","点击菜单消息"),
    /**
     * 点击菜单跳转链接
     */
    VIEW("view","点击菜单跳转链接");

    private final String name;
    private final String desc;
    MessageEventType(@Nonnull final String name,@Nonnull final String desc){
        this.name = name;
        this.desc = desc;
    }

    public static MessageEventType parse(final String name){
        if(!Strings.isNullOrEmpty(name)) {
            for (MessageEventType t : MessageEventType.values()) {
                if (name.equalsIgnoreCase(t.getName())) {
                    return t;
                }
            }
        }
        return null;
    }
}