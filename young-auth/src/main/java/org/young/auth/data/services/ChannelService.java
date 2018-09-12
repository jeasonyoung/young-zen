package org.young.auth.data.services;

import org.young.auth.data.domain.DataChannel;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 渠道-数据服务接口
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/24 16:57
 */
public interface ChannelService {

    /**
     * 缓存键-根据渠道号加载数据
     */
    String CACHE_KEY_CHANNEL_BY_CODE = "auth_data_channel_by_code";

    /**
     * 缓存键-认证处理bean名称数组
     */
    String CACHE_KEY_AUTH_BEANS_BY_CHANNEL = "auth_data_auth_beans_by_channel";

    /**
     * 根据渠道号加载数据。
     * @param channel
     * 渠道号。
     * @return 渠道数据。
     */
    DataChannel loadChannelByCode(@Nonnull final Integer channel);

    /**
     * 根据渠道加载处理认证的Bean名称数组。
     * @param channel
     * 渠道号。
     * @return Bean名称数组。
     */
    List<String> loadAuthBeansByChannel(@Nonnull final Integer channel);
}
