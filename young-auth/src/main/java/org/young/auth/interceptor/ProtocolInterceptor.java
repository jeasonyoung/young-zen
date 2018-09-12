package org.young.auth.interceptor;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.young.auth.services.AuthenService;
import org.young.auth.services.ChannelProviderService;
import org.young.common.interceptor.BaseProtocolVerifyInterceptor;
import org.young.common.protocol.provider.ChannelProvider;
import org.young.common.protocol.provider.TokenProvider;

/**
 * 报文校验拦截器
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/25 10:56
 */
@Aspect
@Component
public class ProtocolInterceptor extends BaseProtocolVerifyInterceptor {

    /**
     * 注入-渠道提供者服务
     */
    @Autowired
    private ChannelProviderService channelProviderService = null;

    /**
     * 注入-认证-服务接口
     */
    @Autowired
    private AuthenService authenService = null;

    /**
     * 获取渠道提供者接口实现。
     * @return 渠道提供者接口实现。
     */
    @Override
    protected ChannelProvider getChannel() {
        return channelProviderService;
    }

    /**
     * 获取令牌提供者接口实现。
     * @return 令牌提供者接口实现。
     */
    @Override
    protected TokenProvider getToken() {
        return authenService;
    }
}
