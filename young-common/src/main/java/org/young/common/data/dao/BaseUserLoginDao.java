package org.young.common.data.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.repository.NoRepositoryBean;
import org.young.common.data.domain.BaseDataUserLogin;

import javax.annotation.Nonnull;

/**
 * 用户登录-数据操作基接口
 *
 * @author yangyong young1982@foxmail.com
 * @date 2018/7/23 11:42
 */
@NoRepositoryBean
public interface BaseUserLoginDao<T extends BaseDataUserLogin> extends BaseDao<T> {

    /**
     * 根据登录令牌加载数据。
     * @param token
     * 登录令牌。
     * @return 登录数据。
     */
    T loadByToken(@Nonnull final String token);

    /**
     * 根据登录刷新令牌加载数据。
     * @param refreshToken
     * 登录刷新令牌。
     * @return 登录数据。
     */
    T loadByRefreshToken(@Nonnull final String refreshToken);

    /**
     * 加载用户最新刷新令牌。
     * @param userId
     * 用户ID。
     * @return 最新刷新令牌。
     */
    String loadLastRefreshTokenByUser(@Nonnull final String userId);

    /**
     * 更新登录状态。
     * @param loginId
     * 登录ID。
     * @param status
     * 登录状态。
     * @return 更新结果。
     */
    int updateStatusByLogin(@Param("loginId") final String loginId, @Param("status") final Integer status);

    /**
     * 更新用户全部登录状态
     * @param userId
     * 用户ID。
     * @param status
     * 登录状态。
     * @return 更新结果。
     */
    int updateAllStatusByUser(@Param("userId") final String userId, @Param("status") final Integer status);
}
