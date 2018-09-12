package org.young.auth.services.impl;

import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.young.auth.config.ChannelBeansProperties;
import org.young.auth.data.services.ChannelService;
import org.young.auth.services.ChannelAuthenBeansService;
import org.young.auth.services.UserAuthenService;
import org.young.common.util.ConvertUtils;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 渠道用户认证处理-服务接口实现
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/26 11:59
 */
@Slf4j
@Service
public class ChannelAuthenBeansServiceImpl implements ChannelAuthenBeansService {

    /**
     * 注入-spring上下文
     */
    @Autowired
    private ApplicationContext context = null;

    /**
     * 注入-配置渠道beans处理
     */
    @Autowired
    private ChannelBeansProperties beansConfig = null;

    /**
     * 注入-数据库渠道Beans处理
     */
    @Autowired
    private ChannelService channelService = null;

    /**
     * 根据渠道加载用户认证处理服务。
     * @param channel
     * 渠道号。
     * @return 用户登录处理服务。
     */
    @Override
    public List<UserAuthenService> loadServiceByChannel(@Nonnull final Integer channel) {
        log.debug("loadServiceByChannel(channel: {})...", channel);
        //检查参数
        Assert.notNull(channel, "'channel'不能为空!");
        Assert.notNull(context, "'context'获取Spring上下文不能为空!");
        //优先获取从数据库配置
        List<String> beanNames = channelService.loadAuthBeansByChannel(channel);
        if(beanNames == null || beanNames.size() == 0){
            //其次从配置文件获取
            //检查配置文件是否已配置
            if(beansConfig != null && beansConfig.getAuthen() != null){
                beanNames = beansConfig.getAuthen().get(channel);
                log.info("配置文件authen配置[channel: {}]=> {}", channel, Joiner.on(',').join(beanNames));
            }
        }
        //检查是否已配置
        Assert.notEmpty(beanNames,"渠道("+ channel +")未配置令牌处理Beans!");
        //从上下获取Beans对象
        return ConvertUtils.convertHandler(beanNames, item -> {
            try {
                final UserAuthenService service = context.getBean(item, UserAuthenService.class);
                log.info("Authen(channel: {})=> {}", channel, service);
                return service;
            }catch (Throwable e){
                log.error("加载渠道["+ channel +"]认证处理bean["+ item +"]失败-exp:" + e.getMessage(), e);
                return null;
            }
        });
    }
}
