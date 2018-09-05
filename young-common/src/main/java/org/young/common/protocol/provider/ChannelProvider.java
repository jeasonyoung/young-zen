package org.young.common.protocol.provider;

import org.young.common.Callback;

import java.io.Serializable;

/**
 * 渠道数据接口
 * @author jeasonyoung
 */
public interface ChannelProvider extends Serializable {

    /**
     * 根据渠道号加载数据
     * @param channelCode
     * 渠道号
     * @return 渠道数据
     */
    Callback<Channel> loadChannelByCode(final Integer channelCode);
}
