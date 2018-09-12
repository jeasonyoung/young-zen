package org.young.auth.data.services;

import org.young.common.Status;
import org.young.common.data.domain.BaseDataUserLogin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * 用户登录-数据服务接口
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/25 11:44
 */
public interface UserLoginService<T extends BaseDataUserLogin> extends Serializable {

    /**
     * 根据令牌加载登录数据。
     * @param token
     * 令牌。
     * @return 登录数据。
     */
    T loadByToken(@Nonnull final String token);

    /**
     * 根据刷新令牌加载登录数据。
     * @param refreshToken
     * 刷新令牌。
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
     * 更新用户登录状态。
     * @param loginId
     * 用户登录ID。
     * @param status
     * 状态。
     * @return 更新结果。
     */
    boolean updateStatus(@Nonnull final String loginId, @Nonnull final Status status);

    /**
     * 更新用户登录令牌。
     * @param loginId
     * 用户登录ID。
     * @param token
     * 用户登录令牌。
     * @return 更新结果。
     */
    boolean updateToken(@Nonnull final String loginId, @Nonnull final String token);

    /**
     * 创建令牌。
     * @param userId
     * 用户ID。
     * @param ipAddr
     * IP地址。
     * @param mac
     * 设备标识。
     * @return 令牌数据。
     */
    T createToken(@Nonnull final String userId, @Nullable final String ipAddr, @Nullable final String mac);

    /**
     * 移除此次登录令牌。
     * @param loginId
     * 登录ID。
     * @return 移除结果。
     */
    boolean removeTokenByLogin(@Nonnull final String loginId);

    /**
     * 移除用户全部登录令牌。
     * @param userId
     * 用户ID。
     * @return 移除结果。
     */
    boolean removeAllTokenByUser(@Nonnull final String userId);
}