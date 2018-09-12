package org.young.auth.services;

import lombok.Data;
import org.young.common.Status;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * 短信验证-服务接口
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/26 18:49
 */
public interface SmsValidService {

    /**
     * 发送短信验证码。
     * @param channel
     * 渠道号。
     * @param mobile
     * 手机号码。
     * @param userSign
     * 用户标识
     * @return 发送反馈。
     */
    SmsResult sendSmsValid(@Nonnull final Integer channel, @Nonnull final String mobile,@Nullable final String userSign);

    /**
     * 校验短信验证码。
     * @param valid
     * 验证码ID。
     * @param code
     * 校验码。
     * @return 校验结果。
     */
    boolean verifySmsValid(@Nonnull final String valid, @Nonnull final String code);

    /**
     * 短信验证码结果
     */
    @Data
    class SmsResult implements Serializable {
        /**
         *  反馈结果
         */
        private Status status;
        /**
         * 反馈代码
         */
        private String retCode;
        /**
         * 反馈消息
         */
        private String retMsg;
        /**
         * 验证码ID
         */
        private String valid;
    }
}
