package org.young.common.interceptor;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.bind.annotation.RequestHeader;
import org.young.common.protocol.CodecUtils;
import org.young.common.protocol.RespStatus;
import org.young.common.protocol.RespUtils;
import org.young.common.protocol.VerifyUtils;
import org.young.common.protocol.provider.ChannelProvider;
import org.young.common.protocol.provider.TokenProvider;
import org.young.common.protocol.request.Request;
import org.young.common.protocol.response.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 通讯报文校验拦截器。
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/25 09:57
 */
@Slf4j
public abstract class BaseProtocolVerifyInterceptor {

    /**
     * 获取比较版本号。
     * @return 比较版本号。
     */
    protected Integer getVersion(){
        return null;
    }

    /**
     * 获取最大请求超时时间。
     * @return 请求最大超时时间(秒)。
     */
    private Integer getMaxTimeout(){
        return 600;
    }

    /**
     * 获取渠道接口。
     * @return 渠道接口。
     */
    protected abstract ChannelProvider getChannel();

    /**
     * 获取令牌接口。
     * @return 令牌接口。
     */
    protected abstract TokenProvider getToken();

    /**
     * 拦截方法入口。
     */
    @Pointcut("execution(public * com.wosai.wso..controller..*.*(..)) and (@annotation(org.springframework.web.bind.annotation.RequestMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping))")
    public void methodPointcut(){
        log.debug("methodPointcut...");
    }

    /**
     * 校验报文拦截器具体实现。
     * @param joinPoint
     * 拦截点。
     * @return 拦截结果。
     */
    @Around("methodPointcut()")
    public final Object verifyInterceptor(final ProceedingJoinPoint joinPoint){
        log.debug("verifyInterceptor(joinPoint: {})...", joinPoint);
        //执行开始时间
        final long startTime = System.currentTimeMillis();
        //拦截方法名/渠道名称
        String methodName = null;
        //返回类型是否符合统一处理
        boolean hasResult = false;
        try{
            //获取拦截方法签名
            final MethodSignature signature = (MethodSignature)joinPoint.getSignature();
            methodName = signature.getDeclaringTypeName() + "." + signature.getName();
            //拦截标识
            boolean isInterceptor = false;
            //获取拦截方法参数
            final Object[] args = joinPoint.getArgs();
            if(args != null && args.length > 0) {
                //获取拦截方法
                final Method method = signature.getMethod();
                //检查返回类型是否为Response报文类型
                if(method.getReturnType() == Response.class) {
                    hasResult = true;
                    //拦截处理
                    Response<?> resp = null;
                    //第一个参数为请求报文
                    if (args[0] instanceof Request<?>) {
                        isInterceptor = true;
                        //拦截报文校验处理
                        resp = verifyProtocolHandler(methodName, (Request<?>) args[0]);
                    } else if (args[0] instanceof String) {
                        try {
                            final Parameter parameter = method.getParameters()[0];
                            if (parameter != null && parameter.getAnnotation(RequestHeader.class) != null) {
                                //报文源码
                                final String source = (String)args[0];
                                //报文解码
                                final String decoder = CodecUtils.decode(source);
                                log.info("verifyInterceptor-decode=>\n {}", decoder);
                                //将字符串反序列化为对象
                                final Request req = JSON.parseObject(decoder, Request.class);
                                if (req != null && req.getHead() != null && req.getBody() != null) {
                                    isInterceptor = true;
                                    //报文校验处理
                                    resp = verifyProtocolHandler(methodName, req);
                                }
                            }
                        } catch (Throwable e) {
                            log.warn("verifyInterceptor-exp" + e.getMessage());
                        }
                    }
                    //校验报文失败
                    if (resp != null) {
                        //返回失败报文
                        return resp;
                    }
                }
            }
            final Object result = joinPoint.proceed(args);
            if(isInterceptor && (result instanceof Serializable)){
                log.info("response[methodName: {}]: \n {}", methodName, JSON.toJSONString(result));
            }
            return result;
        }catch (Throwable e){
            log.warn("verifyInterceptor[methodName:"+ methodName +"]-exp:" + e.getMessage(), e);
            if(hasResult) {
                return RespUtils.createResponse(RespStatus.ErrWithProtocolVerify, e.getMessage());
            }
        }finally {
            //计算耗时
            final long totals = System.currentTimeMillis() - startTime;
            log.info("verifyInterceptor[methodName: {}]-耗时: {} ms", methodName, totals);
        }
        return null;
    }

    /**
     * 校验报文处理。
     * @param methodName
     * 方法名称
     * @param request
     * 请求报文对象。
     * @return 校验结果。
     */
    private Response<?> verifyProtocolHandler(@Nonnull final String methodName, @Nonnull final Request<?> request){
        //打印拦截报文
        log.info("request[methodName:{}]:\n {}", methodName, JSON.toJSONString(request));
        //校验处理
        final Response<?> resp = VerifyUtils.verifyRequest(request, new VerifyRequestListenerImpl());
        if(resp != null){
            log.info("[methodName: {}]:报文校验失败!\n response:\n {}", methodName, JSON.toJSONString(resp));
            return resp;
        }
        log.info("[methodName: {}]:报文校验成功!", methodName);
        return null;
    }

    /**
     * 校验请求报文监听器内部实现
     */
    private class VerifyRequestListenerImpl implements VerifyUtils.VerifyRequestListener {

        @Override
        public Integer getCurrentAPIVersion() {
            return getVersion();
        }

        @Override
        public Integer getTimeout() {
            return getMaxTimeout();
        }

        @Override
        public ChannelProvider getChannelProvider() {
            return getChannel();
        }

        @Override
        public TokenProvider getTokenProvider() {
            return getToken();
        }

        @Override
        public void setChannel(@Nonnull final Integer channelCode, @Nullable final String channelName) {
            log.info("verifyRequest(channelCode: {}, channelName: {})...", channelCode, channelName);
        }
    }
}