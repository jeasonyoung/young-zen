package org.young.wechat.message;

import com.google.common.base.Strings;
import lombok.Getter;

import javax.annotation.Nonnull;

/**
 * 普通类型
 * @author jeasonyoung
 */
@Getter
public enum MessageType {
    /**
     * 文本消息
     */
    TEXT("text","文本消息"),
    /**
     * 图片消息
     */
    IMAGE("image","图片消息"),
    /**
     * 语音消息
     */
    VOICE("voice","语音消息"),
    /**
     * 视频消息
     */
    VIDEO("video","视频消息"),
    /**
     * 小视频消息
     */
    SHORTVIDEO("shortvideo","小视频消息"),
    /**
     * 地理位置消息
     */
    LOCATION("location","地理位置消息"),
    /**
     * 链接消息
     */
    LINK("link","链接消息"),
    /**
     * 事件推送
     */
    EVENT(DataType.Event,"event","事件推送");

    private final DataType type;
    private final String name,desc;
    MessageType(final DataType type,final String name,final String desc){
        this.type = type;
        this.name = name;
        this.desc = desc;
    }
    MessageType(@Nonnull final String name, @Nonnull final String desc){
        this(DataType.Normal, name, desc);
    }

    /**
     * 消息类型枚举类型解析。
     * @param name
     * 类型名称。
     * @return 消息类型。
     */
    public static MessageType parse(final String name){
        if(!Strings.isNullOrEmpty(name)) {
            for (MessageType t : MessageType.values()) {
                if (name.equalsIgnoreCase(t.getName())) {
                    return t;
                }
            }
        }
        return null;
    }
}