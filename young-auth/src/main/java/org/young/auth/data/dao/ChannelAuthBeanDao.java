package org.young.auth.data.dao;

import org.young.auth.data.domain.DataChannelAuthBean;
import org.young.common.data.dao.BaseDao;

import java.util.List;

/**
 * 渠道认证关联处理器-数据操作
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/24 16:36
 */
public interface ChannelAuthBeanDao extends BaseDao<DataChannelAuthBean> {

    /**
     * 根据渠道ID加载数据集合。
     * @param channelId
     * 渠道ID。
     * @return 处理器数据集合。
     */
    List<DataChannelAuthBean> loadAllByChannel(final String channelId);
}
