package org.young.common.data.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.repository.NoRepositoryBean;
import org.young.common.data.domain.BaseDataUserWechat;

/**
 * 用户关联微信-数据操作基接口
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/23 11:42
 */
@NoRepositoryBean
public interface BaseUserWechatDao<T extends BaseDataUserWechat> extends BaseDao<T> {

    /**
     * 根据OpenID加载数据。
     * @param openId
     * 关联 Open ID。
     * @return 关联数据。
     */
    T loadLastByOpen(final String openId);

    /**
     * 根据用户ID加载数据
     * @param userId
     * 用户ID
     * @return 关联数据
     */
    T loadLastByUser(final String userId);

    /**
     * 根据用户和OpenID加载关联数据。
     * @param userId
     * 用户ID。
     * @param openId
     * OpenID。
     * @return 关联数据。
     */
    T loadByUserAndOpen(@Param("userId") final String userId, @Param("openId") final String openId);

    /**
     * 更新状态。
     * @param userId
     * 用户ID。
     * @param status
     * 状态。
     * @return 更新结果。
     */
    int updateStatusByUser(@Param("userId") final String userId, @Param("status") final Integer status);
}
