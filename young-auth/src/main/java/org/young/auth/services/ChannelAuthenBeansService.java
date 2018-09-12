package org.young.auth.services;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 渠道用户认证处理-服务接口
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/26 11:33
 */
public interface ChannelAuthenBeansService {

    /**
     * 根据渠道加载用户认证处理服务。
     * @param channel
     * 渠道号。
     * @return 用户登录处理服务。
     */
    List<UserAuthenService> loadServiceByChannel(@Nonnull final Integer channel);
}