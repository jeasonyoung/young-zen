package org.young.common.protocol.response;

import lombok.Data;
import org.young.common.protocol.RespStatus;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * 响应报文体基类
 *
 * @author yangyong young1982@foxmail.com
 * @date 2018/7/23 17:09
 */
@Data
public abstract class BaseRespBody implements Serializable {
    /**
     * 反馈代码
     */
    private Integer code;
    /**
     * 反馈提示消息
     */
    private String msg;

    /**
     * 构建响应状态反馈代码。
     * @param status
     * 响应状态枚举对象
     */
    public void buildRespStatusCode(@Nonnull final RespStatus status){
        //设置反馈代码
        this.setCode(status.getCode());
        //设置反馈消息
        this.setMsg(status.getMsg());
    }
}