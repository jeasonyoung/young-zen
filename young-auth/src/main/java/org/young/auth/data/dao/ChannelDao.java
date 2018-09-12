package org.young.auth.data.dao;

import org.young.auth.data.domain.DataChannel;
import org.young.common.data.dao.BaseDao;

/**
 * 渠道-数据操作
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/24 16:34
 */
public interface ChannelDao extends BaseDao<DataChannel> {

    /**
     * 根据渠道加载数据。
     * @param code
     * 渠道号。
     * @return 渠道数据。
     */
    DataChannel loadByCode(final Integer code);
}
