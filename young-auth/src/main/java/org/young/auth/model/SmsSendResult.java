package org.young.auth.model;

import lombok.Data;
import org.young.common.Status;

import java.io.Serializable;

/**
 * 短信发送结果
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/9/11 23:06
 */
@Data
public class SmsSendResult implements Serializable {
    /**
     * 反馈状态
     */
    private Status status;
    /**
     * 反馈代码
     */
    private String code;
    /**
     * 反馈消息
     */
    private String msg;
}
