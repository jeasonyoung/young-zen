package org.young.auth.event;

import lombok.Data;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

/**
 * SMS 验证码事件
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/28 01:31
 */
@Getter
public class SmsValidEvent extends ApplicationEvent {
    private ValidData data;

    /**
     * 构造函数。
     * @param source
     * 事件源。
     * @param data
     * 事件数据。
     */
    public SmsValidEvent(final Object source, final ValidData data) {
        super(source);
        this.data = data;
    }

    /**
     * 验证数据
     */
    @Data
    public static class ValidData implements Serializable {
        /**
         * 渠道号
         */
        private Integer channel;
        /**
         * 手机号码
         */
        private String mobile;
        /**
         * 用户标识
         */
        private String userSign;
    }
}
