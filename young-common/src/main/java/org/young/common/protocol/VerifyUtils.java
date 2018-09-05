package org.young.common.protocol;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.young.common.Callback;
import org.young.common.Status;
import org.young.common.protocol.provider.*;
import org.young.common.protocol.request.ReqHead;
import org.young.common.protocol.request.ReqIgnoreTokenVerify;
import org.young.common.protocol.request.Request;
import org.young.common.protocol.response.Response;
import org.young.common.util.SessionConstants;
import org.young.common.util.SignatureUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

/**
 * 报文校验工具
 * @author jeasonyoung
 */
@Slf4j
public class VerifyUtils {

    /**
     * 验证请求报文。
     * @param request
     * 请求报文。
     * @param listener
     * 请求验证监听器。
     * @return 返回null则通过验证。
     */
    public static Response<? extends Serializable> verifyRequest(@Nonnull final Request<? extends Serializable> request, @Nonnull final VerifyRequestListener listener){
        log.debug("verifyRequest(request: {},listener: {})...", request, listener);
        final long start = System.currentTimeMillis();
        String channelName = null;
        try{
            //获取报文头
            final ReqHead head = request.getHead();
            //检查报文头
            if(head == null){
                log.warn("获取报文头失败!");
                return RespUtils.createResponse(RespStatus.ExpWithHead);
            }
            Response<?> resp;
            //校验版本号
            if((resp = verifyVersion(head, listener)) != null){
                log.warn("校验版本号失败!");
                return resp;
            }
            //校验时间戳
            if((resp = verifyTime(head, listener)) != null){
                log.warn("校验时间戳失败!");
                return resp;
            }
            //签名验证类型
            VerifyType verify = VerifyType.Not;
            //校验渠道
            final VerifyChannelResult result = verifyChannel(head, listener.getChannelProvider());
            if(result != null){
                //检查是否校验失败
                if(result.getResponse() != null){
                    return result.getResponse();
                }
                //渠道名称
                channelName = result.getChannelName();
                //渠道数据设置
                listener.setChannel(head.getChannel(), channelName);
                //签名是否校验
                if(result.getType() != null){
                    verify = result.getType();
                }
            }
            //检查是否校验签名
            if(verify == VerifyType.Has && (resp = verifySign(request)) != null){
                log.warn("校验签名失败");
                return resp;
            }
            //检查是否校验令牌
            if(!(request instanceof ReqIgnoreTokenVerify)){
                try {
                    //校验用户登录令牌
                    resp = verifyToken(head, listener.getTokenProvider());
                    if (resp != null) {
                        return resp;
                    }
                }catch (Throwable e){
                    log.warn("verifyRequest-verifyToken(head: {})-exp: {}", head, e.getMessage());
                    return RespUtils.createResponse(RespStatus.TokenInvalid);
                }
            }
        }catch (Throwable e){
            log.warn("verifyRequest(request: {})-exp:{}",request, e.getMessage());
        }finally {
            final String totals = (System.currentTimeMillis() - start) + " ms ";
            log.info("verifyRequest(channelName:{})- run time: {}", channelName, totals);
        }
        return null;
    }

    /**
     * 校验版本。
     * @param head
     * 报文头。
     * @param listener
     * 监听处理器。
     * @return 校验结果
     */
    private static Response<? extends Serializable> verifyVersion(@Nonnull final ReqHead head, @Nonnull final VerifyRequestListener listener){
        //检查请求头版本号
        if(head.getVersion() == null || head.getVersion() < 1){
            log.warn("版本号(head-version: {})-错误!", head.getVersion());
            return RespUtils.createResponse(RespStatus.ErrWithVersion);
        }
        //比较目标版本号
        if(listener.getCurrentAPIVersion() != null && listener.getCurrentAPIVersion() > 0 && head.getVersion().equals(listener.getCurrentAPIVersion())){
            log.warn("版本号(head-version: {}, current: {})-不一致!", head.getVersion(), listener.getCurrentAPIVersion());
            return RespUtils.createResponse(RespStatus.ErrWithVersion);
        }
        return null;
    }

    /**
     * 校验时间戳。
     * @param head
     * 报文头。
     * @param listener
     * 监听处理器
     * @return 校验结果
     */
    private static Response<? extends Serializable> verifyTime(@Nonnull final ReqHead head, @Nonnull final VerifyRequestListener listener) {
        //检查时间戳
        if(head.getTime() <= 0){
            log.warn("时间戳({})-格式错误!", head.getTime());
            return RespUtils.createResponse(RespStatus.TimeInvalid);
        }
        //检查时间戳是否过期
        final long interval, current = System.currentTimeMillis(), timeout = listener.getTimeout();
        final long secMsec = 1000;
        if(timeout > 0 && ((interval = (current - head.getTime())) > timeout * secMsec)) {
            log.warn("时间戳过期(current: {}, req-time: {}, interval: {})", current, head.getTime(), interval);
            return RespUtils.createResponse(RespStatus.ExpireWithTime);
        }
        return null;
    }

    /**
     * 校验渠道结果
     */
    @Data
    private static class VerifyChannelResult implements Serializable {
        private VerifyType type;
        private String channelName;
        private Response<? extends Serializable> response;
    }

    /**
     * 验证渠道。
     * @param head
     * 消息头。
     * @param channelProvider
     * 渠道数据接口。
     * @return 验证结果。
     */
    private static VerifyChannelResult verifyChannel(@Nonnull final ReqHead head, @Nonnull final ChannelProvider channelProvider){
        log.debug("verifyChannel: {}, channelProvider: {}", head, channelProvider);
        //初始化验证结果
        final VerifyChannelResult result = new VerifyChannelResult();
        //检查渠道号
        if(head.getChannel() == null || head.getChannel() < 0){
            log.warn("verifyChannel-渠道号({})错误!", head.getChannel());
            result.setResponse(RespUtils.createResponse(RespStatus.EmptyWithChannel));
            return result;
        }
        //加载渠道数据
        final Callback<Channel> callback = channelProvider.loadChannelByCode(head.getChannel());
        if(callback == null || callback.getData() == null){
            log.warn("verifyChannel-渠道号({})不存在=>{}", head.getChannel(), callback);
            if(callback != null) {
                final RespStatus status = RespStatus.parse(callback.getCode());
                result.setResponse(status == null ?
                        RespUtils.createResponse(RespStatus.NotExistWithChannel, callback.getMessage())
                        : RespUtils.createResponse(status));
            }else{
                result.setResponse(RespUtils.createResponse(RespStatus.NotExistWithChannel));
            }
            return result;
        }
        //检查渠道状态
        final Channel channel = callback.getData();
        if(channel.getStatus() == Status.Disabled){
            log.warn("渠道(channel: {})-已禁用", head.getChannel());
            result.setResponse(RespUtils.createResponse(RespStatus.DisabledWithChannel));
            return result;
        }
        //设置渠道名称
        result.setChannelName(channel.getName() + "["+ channel.getAbbr() +"]");
        //设置验证类型
        result.setType(channel.getVerify());
        //返回验证结果
        return result;
    }

    /**
     * 验证参数签名。
     * @param request
     * 请求报文。
     * @return 验证结果。
     */
    private static Response<? extends Serializable> verifySign(@Nonnull final Request<? extends Serializable> request) {
        try {
            //json化处理
            final JSONObject jsonObj = (JSONObject) JSON.toJSON(request);
            if(jsonObj == null){
                log.warn("fastjson将Java对象转换为JSON对象失败!");
                return RespUtils.createResponse(RespStatus.ExpWithHead);
            }
            //获取报文头
            final ReqHead head = request.getHead();
            //计算参数签名
            final String newSign = SignatureUtils.createSignature(jsonObj, head.getTime() + "");
            if(!newSign.equalsIgnoreCase(head.getSign())){
                log.warn("verifySign(newSign: {}, oldSign: {})-签名错误!", newSign, head.getSign());
                return RespUtils.createResponse(RespStatus.ErrWithSign);
            }
            return null;
        }catch (Throwable e){
            log.warn("验证参数签名失败-exp: {}", e.getMessage());
            return RespUtils.createResponse(RespStatus.ErrWithSign);
        }
    }

    /**
     * 校验令牌。
     * @param head
     * 请求消息头。
     * @param tokenProvider
     * 令牌数据接口。
     * @return 验证结果。
     */
    private static Response<? extends Serializable> verifyToken(@Nonnull final ReqHead head, @Nonnull final TokenProvider tokenProvider) {
        //加载令牌用户
        final Callback<TokenUser> callback = tokenProvider.loadUserByToken(head.getChannel(), head.getToken());
        if (callback == null) {
            log.warn("verifyToken(channel: {}, token: {})-令牌无效!", head.getChannel(), head.getToken());
            return RespUtils.createResponse(RespStatus.TokenInvalid);
        }
        //检查令牌用户数据
        if(callback.getData() == null){
            log.warn("verifyToken(head: {}, callback: {})-没有令牌数据!", head, callback);
            final RespStatus respStatus = RespStatus.parse(callback.getCode());
            return respStatus == null ?
                    RespUtils.createResponse(RespStatus.TokenInvalid, callback.getMessage())
                    : RespUtils.createResponse(respStatus);
        }
        //加载http会话
        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            final HttpServletRequest servletRequest = requestAttributes.getRequest();
            final HttpSession session = servletRequest.getSession();
            if (session != null) {
                //缓存到session
                session.setAttribute(SessionConstants.SESSION_TOKEN_USER_KEY, callback.getData());
            }
        }
        return null;
    }

    /**
     * 请求报文校验监听器
     */
    public interface VerifyRequestListener {

        /**
         * 获取当前API版本。
         * @return 当前API版本。
         */
        default Integer getCurrentAPIVersion(){ return 1; }

        /**
         * 获取时间戳有效期(秒)。
         * @return 时间戳有效期(秒)。
         */
        default Integer getTimeout() { return -1;}

        /**
         * 设置渠道号和名称。
         * @param channelCode
         * 渠道号。
         * @param channelName
         * 渠道名称。
         */
        default void setChannel(@Nonnull final Integer channelCode, @Nullable final String channelName) {}

        /**
         * 获取渠道服务接口。
         * @return 渠道服务接口。
         */
        ChannelProvider getChannelProvider();

        /**
         * 获取令牌服务接口。
         * @return 令牌服务接口。
         */
        TokenProvider getTokenProvider();
    }
}