package org.young.auth.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

import javax.annotation.Nonnull;

/**
 * 认证-事件监听处理器
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/31 09:59
 */
@Slf4j
public abstract class BaseAuthenEventListener implements ApplicationListener<AuthenEvent> {

    /**
     * 监听事件处理
     * @param event
     * 事件数据
     */
    @Override
    public void onApplicationEvent(@Nonnull final AuthenEvent event) {
        log.debug("onApplicationEvent(event: {})...", event);
        //渠道号
        final Integer channel = event.getChannel();
        //用户ID
        final String userId = event.getUserId();
        //类型
        switch (event.getType()){
            //登录
            case Login:{
                //登录处理
                userLoginHandler(channel, userId);
                return;
            }
            //注销
            case Logout:{
                //注销处理
                userLogoutHandler(channel, userId);
                return;
            }
            default:break;
        }
    }

    /**
     * 用户登录处理
     * @param channel
     * 渠道号
     * @param userId
     * 用户ID
     */
    protected abstract void userLoginHandler(@Nonnull final Integer channel, @Nonnull final String userId);

    /**
     * 用户注销处理
     * @param channel
     * 渠道号
     * @param userId
     * 用户ID
     */
    protected abstract void userLogoutHandler(@Nonnull final Integer channel, @Nonnull final String userId);
}
