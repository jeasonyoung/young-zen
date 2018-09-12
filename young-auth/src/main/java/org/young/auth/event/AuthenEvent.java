package org.young.auth.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

/**
 * 认证事件。
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/31 09:43
 */
@Getter
@Setter
public class AuthenEvent extends ApplicationEvent {
    /**
     * 认证类型
     */
    private AuthenEventType type;
    /**
     * 设置渠道号
     */
    private Integer channel;
    /**
     * 用户ID。
     */
    private String userId;

    /**
     * 构造函数。
     * @param source
     * 事件触发对象。
     * @param type
     * 认证类型。
     */
    public AuthenEvent(final Object source, final AuthenEventType type) {
        super(source);
        this.type = type;
    }

    /**
     * 认证事件类型
     */
    public enum AuthenEventType implements Serializable {
        /**
         * 登录
         */
        Login,
        /**
         * 注销
         */
        Logout
    }
}
