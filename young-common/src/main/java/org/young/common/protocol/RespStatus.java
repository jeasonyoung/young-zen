package org.young.common.protocol;

import lombok.Getter;

import java.io.Serializable;

/**
 * 响应状态枚举
 * @author jeasonyoung
 */
@Getter
public enum RespStatus implements Serializable {
    /**
     * 成功
     */
    Success(0, "成功"),
    /**
     * 未知失败
     */
    Unknown(-1, "未知失败"),
    /**
     * 失败
     */
    Failure(-2, "失败"),
    /**
     * 消息头异常
     */
    ExpWithHead(100, "消息头异常"),
    /**
     * 版本错误
     */
    ErrWithVersion(110, "版本错误"),
    /**
     * 版本更新
     */
    UpdateVersion(111, "版本更新"),
    /**
     * 渠道号为空
     */
    EmptyWithChannel(120, "渠道号为空"),
    /**
     * 渠道号不存在
     */
    NotExistWithChannel(121, "渠道号不存在"),
    /**
     * 渠道未启用
     */
    DisabledWithChannel(122, "渠道未启用"),
    /**
     * 令牌无效
     */
    TokenInvalid(140, "令牌无效"),
    /**
     * 令牌过期
     */
    TokenExpire(141, "令牌过期"),
    /**
     * 刷新令牌无效
     */
    RefreshTokenInvalid(142, "刷新令牌无效"),
    /**
     * 时间戳过期
     */
    ExpireWithTime(150, "时间戳过期"),
    /**
     * 时间戳无效
     */
    TimeInvalid(151, "时间戳无效"),
    /**
     * 签名为空
     */
    EmptyWithSign(160, "签名为空"),
    /**
     * 签名错误
     */
    ErrWithSign(161,"签名错误"),
    /**
     * 服务器错误
     */
    ErrWithServer(500,"服务器错误"),
    /**
     * 校验报文错误
     */
    ErrWithProtocolVerify(501,"校验报文错误");

    private int code;
    private String msg;

    /**
     * 构造函数。
     * @param code
     * 状态码。
     * @param msg
     * 状态描述。
     */
    RespStatus(final int code, final String msg){
        this.code = code;
        this.msg = msg;
    }

    /**
     * 枚举类型转换。
     * @param code
     * 枚举值。
     * @return
     * 枚举类型。
     */
    public static RespStatus parse(final int code){
        for(RespStatus s : RespStatus.values()){
            if(s.getCode() == code){
                return s;
            }
        }
        return RespStatus.Success;
    }
}
