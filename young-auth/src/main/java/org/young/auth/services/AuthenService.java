package org.young.auth.services;

import org.young.auth.model.UserAccount;
import org.young.auth.model.UserCertificate;
import org.young.auth.model.UserInfo;
import org.young.common.exception.AuthenException;
import org.young.common.exception.TokenException;
import org.young.common.protocol.provider.TokenProvider;
import org.young.common.protocol.provider.TokenUserData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 认证-服务接口
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/24 15:23
 */
public interface AuthenService extends TokenProvider {

    /**
     * 用户认证。
     * @param channel
     * 渠道号。
     * @param account
     * 登录账号。
     * @param password
     * 登录密码。
     * @param mac
     * 设备标识。
     * @param validId
     * 验证码ID。
     * @param validCode
     * 验证码。
     * @return 认证结果。
     * @throws AuthenException
     * 认证异常。
     */
    UserCertificate authen(@Nonnull final Integer channel, @Nonnull final String account, @Nullable final String password, @Nullable final String mac, @Nullable final String validId, @Nullable final String validCode) throws AuthenException;

    /**
     * 退出登录。
     * @param channel
     * 渠道号。
     * @param userId
     * 用户ID。
     * @return 退出结果。
     */
    boolean logout(@Nonnull final Integer channel, @Nonnull final String userId);

    /**
     * 用户修改密码。
     * @param channel
     * 渠道号。
     * @param userId
     * 用户ID。
     * @param oldPasswd
     * 旧密码。
     * @param newPasswd
     * 新密码。
     * @return 修改结果。
     * @throws Exception
     * 修改异常。
     */
    boolean modifyPassword(@Nonnull final Integer channel, @Nonnull final String userId, @Nonnull final String oldPasswd, @Nonnull final String newPasswd) throws Exception;

    /**
     * 强制修改密码。
     * @param channel
     * 渠道号。
     * @param userId
     * 用户ID。
     * @param newPasswd
     * 新密码。
     * @return 修改结果。
     * @throws Exception
     * 修改异常。
     */
    boolean forceModifyPassword(@Nonnull final Integer channel, @Nonnull final String userId, @Nonnull final String newPasswd) throws Exception;

    /**
     * 重置密码。
     * @param channel
     * 渠道号。
     * @param userId
     * 用户ID
     * @return 重置结果。
     * @throws Exception
     * 异常。
     */
    boolean resetPasswd(@Nonnull final Integer channel, @Nonnull final String userId) throws Exception;

    /**
     * 注册账号。
     * @param channel
     * 渠道号。
     * @param account
     * 账号。
     * @param passwd
     * 密码。
     * @param mobile
     * 手机号码
     * @return 注册结果。
     * @throws AuthenException
     * 注册异常。
     */
    UserAccount register(@Nonnull final Integer channel, @Nonnull final String account, @Nullable final String passwd, @Nullable final String mobile) throws AuthenException;

    /**
     * 加载用户数据.
     * @param channel
     * 渠道号。
     * @param userId
     * 用户ID。
     * @return 用户数据。
     */
    UserInfo loadUserById(@Nonnull final Integer channel, @Nonnull final String userId);

    /**
     * 根据渠道刷新令牌加载用户数据。
     * @param channel
     * 渠道号。
     * @param refreshToken
     * 登录刷新令牌。
     * @return 用户数据。
     * @throws TokenException
     * 令牌异常。
     */
    TokenUserData loadUserByRefreshToken(@Nonnull final Integer channel, @Nonnull final String refreshToken) throws TokenException;

    /**
     * 加载渠道用户最新刷新令牌。
     * @param channel
     * 渠道号。
     * @param userId
     * 用户ID。
     * @return 最新刷新令牌。
     */
    String loadLastRefreshTokenByUser(@Nonnull final Integer channel, @Nonnull final String userId);
}