package org.young.auth.services;

import org.young.common.protocol.provider.ChannelProvider;

/**
 * 渠道-服务接口
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/25 16:42
 */
public interface ChannelProviderService extends ChannelProvider {

    /**
     * 缓存键-根据渠道号加载渠道数据
     */
    String CACHE_KEY_CHANNEL_BY_CODE = "yonug_auth_srv_channel_by_code";

}
