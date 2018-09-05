package org.young.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 回调反馈
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/9/5 15:15
 */
@Data
public class Callback<T extends Serializable> implements Serializable {
    /**
     * 反馈代码
     */
    private Integer code;
    /**
     * 反馈消息
     */
    private String message;
    /**
     * 反馈数据
     */
    private T data;
}
