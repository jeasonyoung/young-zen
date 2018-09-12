package org.young.auth.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.young.auth.data.domain.DataChannel;
import org.young.auth.data.services.ChannelService;
import org.young.auth.services.ChannelProviderService;
import org.young.common.Callback;
import org.young.common.Status;
import org.young.common.protocol.provider.Channel;
import org.young.common.protocol.provider.VerifyType;

import javax.annotation.Nonnull;

/**
 * 渠道-提供者服务接口实现
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/25 14:15
 */
@Slf4j
@Service
public class ChannelProviderServiceImpl implements ChannelProviderService {

    /**
     * 注入-渠道-数据服务接口
     */
    @Autowired
    private ChannelService channelService = null;

    /**
     * 根据渠道号加载数据。
     * @param channelCode
     * 渠道号。
     * @return 渠道数据。
     */
    @Cacheable(value = { CACHE_KEY_CHANNEL_BY_CODE }, key = "#channelCode")
    @Override
    public Callback<Channel> loadChannelByCode(@Nonnull final Integer channelCode) {
        log.debug("loadChannelByCode(channelCode: {})...", channelCode);
        //
        final Callback<Channel> callback = new Callback<>();
        final DataChannel item = channelService.loadChannelByCode(channelCode);
        if(item != null){
            //初始化渠道数据
            final Channel row = new Channel();
            //设置渠道号
            row.setCode(item.getCode());
            //设置渠道名称
            row.setName(item.getName());
            //设置渠道简称
            row.setAbbr(item.getAbbr());
            //设置渠道类型
            row.setVerify(VerifyType.parse(item.getType()));
            //设置渠道状态
            row.setStatus(Status.parse(item.getStatus()));
            //返回
            callback.setData(row);
        }
        return callback;
    }
}
