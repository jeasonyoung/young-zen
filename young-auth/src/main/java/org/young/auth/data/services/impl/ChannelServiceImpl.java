package org.young.auth.data.services.impl;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.young.auth.data.dao.ChannelAuthBeanDao;
import org.young.auth.data.dao.ChannelDao;
import org.young.auth.data.domain.DataChannel;
import org.young.auth.data.services.ChannelService;
import org.young.common.data.services.impl.BaseDataService;
import org.young.common.util.ConvertUtils;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 渠道-数据服务接口实现
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/24 17:04
 */
@Slf4j
@Repository
public class ChannelServiceImpl extends BaseDataService implements ChannelService {

    /**
     * 注入-渠道-数据操作
     */
    @Autowired
    private ChannelDao channelDao = null;

    /**
     * 注入-认证Beans-数据操作
     */
    @Autowired
    private ChannelAuthBeanDao authBeanDao = null;

    /**
     * 根据渠道号加载数据。
     * @param channel
     * 渠道号。
     * @return 渠道数据。
     */
    @Cacheable(value = { CACHE_KEY_CHANNEL_BY_CODE }, key = "#channel")
    @Override
    public DataChannel loadChannelByCode(@Nonnull final Integer channel) {
        log.debug("loadChannelByCode(channel: {})...", channel);
        return channelDao.loadByCode(channel);
    }

    /**
     * 根据渠道加载处理认证的Bean名称数组。
     * @param channel
     * 渠道号。
     * @return Bean名称数组。
     */
    @Cacheable(value = { CACHE_KEY_AUTH_BEANS_BY_CHANNEL }, key = "#channel")
    @Override
    public List<String> loadAuthBeansByChannel(@Nonnull final Integer channel) {
        log.debug("loadAuthBeansByChannel(channel: {})...", channel);
        //检查参数
        Assert.notNull(channel, "'channel'不能为空!");
        //加载渠道数据
        final DataChannel dataChannel = loadChannelByCode(channel);
        if(dataChannel == null){
            log.error("loadAuthBeansByChannel(channel: {})-渠道不存在!", channel);
            return null;
        }
        //加载渠道Beans数据
        return ConvertUtils.convertHandler(authBeanDao.loadAllByChannel(dataChannel.getId()), item -> {
            final String beanName = item.getBeanName();
            if(!Strings.isNullOrEmpty(beanName)){
                return beanName;
            }
            return null;
        });
    }
}
