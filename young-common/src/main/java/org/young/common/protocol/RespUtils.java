package org.young.common.protocol;

import com.google.common.base.Strings;
import org.young.common.protocol.response.RespHead;
import org.young.common.protocol.response.RespVersion;
import org.young.common.protocol.response.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * 响应报文工具
 * @author jeasonyoung
 */
public class RespUtils {
    /**
     * 创建响应报文。
     * @param status
     * 响应状态枚举。
     * @param <RespBody>
     *     响应报文体类型。
     * @return 响应报文。
     */
    public static <RespBody extends Serializable> Response<RespBody> createResponse(@Nonnull final RespStatus status){
        return createResponse(status, (String)null);
    }

    /**
     * 创建响应报文。
     * @param respStatus
     * 响应状态。
     * @param message
     * 响应消息。
     * @param <RespBody>
     *     报文体类型。
     * @return 响应报文
     */
    public static <RespBody extends Serializable> Response<RespBody> createResponse(@Nonnull final RespStatus respStatus,@Nullable final String message){
        return createResponse(respStatus, null, null, message);
    }

    /**
     * 创建响应报文。
     * @param status
     * 响应状态枚举。
     * @param updateVersion
     * 最新版本数据。
     * @param <RespBody>
     *     响应报文体类型。
     * @return 响应报文。
     */
    public static <RespBody extends Serializable> Response<RespBody> createResponse(@Nonnull final RespStatus status, @Nullable final RespVersion updateVersion){
        return createResponse(status, updateVersion, null, null);
    }

    /**
     * 创建响应报文。
     * @param status
     * 响应状态枚举。
     * @param respBody
     * 响应报文体。
     * @param <RespBody>
     *     响应报文体类型。
     * @return 响应报文。
     */
    public static <RespBody extends Serializable> Response<RespBody> createResponse(@Nonnull final RespStatus status,final RespBody respBody) {
        return createResponse(status, null, respBody, null);
    }

    /**
     * 创建响应报文。
     * @param status
     * 响应状态枚举。
     * @param updateVersion
     * 更新的版本数据
     * @param respBody
     * 响应报文体。
     * @param message
     * 响应消息。
     * @param <RespBody>
     *     响应报文体类型。
     * @return 响应报文。
     */
    private static <RespBody extends Serializable> Response<RespBody> createResponse(@Nonnull final RespStatus status, @Nullable final RespVersion updateVersion, @Nullable final RespBody respBody,@Nullable final String message){
        //初始化响应报文
        final Response<RespBody> resp = new Response<>();
        //初始化响应报文头
        final RespHead respHead = new RespHead();
        //设置响应码
        respHead.setCode(status.getCode());
        //设置响应消息
        if(Strings.isNullOrEmpty(message)) {
            respHead.setMsg(status.getMsg());
        }else{
            respHead.setMsg(status.getMsg() + ":" + message);
        }
        //检查版本数据
        if(updateVersion != null){
            //设置最新版本数据
            respHead.setVersion(updateVersion);
        }
        //设置响应报文头
        resp.setHead(respHead);
        //检查响应报文体
        if(respBody != null){
            //设置响应报文体
            resp.setBody(respBody);
        }
        //返回
        return resp;
    }
}
