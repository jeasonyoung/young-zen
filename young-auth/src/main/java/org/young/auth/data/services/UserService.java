package org.young.auth.data.services;

import org.young.common.data.domain.BaseDataUser;
import org.young.common.exception.AuthenException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * 用户-数据服务接口
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/24 09:11
 */
public interface UserService<T extends BaseDataUser> extends Serializable {

    /**
     * 根据用户ID加载数据.
     *
     * @param userId 用户ID。
     * @return 用户数据。
     */
    T loadUserById(@Nonnull final String userId);

    /**
     * 根据用户账号加载数据。
     *
     * @param account 用户账号。
     * @return 用户数据。
     */
    T loadUserByAccount(@Nonnull final String account);

    /**
     * 验证密码。
     * @param userId
     * 用户ID。
     * @param password
     * 密码。
     * @return 验证结果。
     * @throws AuthenException
     * 验证异常。
     */
    boolean verifyPassword(@Nonnull final String userId, @Nonnull final String password) throws AuthenException;

    /**
     * 用户修改密码。
     *
     * @param userId    用户ID。
     * @param oldPasswd 旧密码。
     * @param newPasswd 新密码。
     * @return 修改结果。
     * @throws Exception 修改异常。
     */
    boolean modifyPassword(@Nonnull final String userId, @Nonnull final String oldPasswd, @Nonnull final String newPasswd) throws Exception;

    /**
     * 强制修改密码。
     *
     * @param userId    用户ID。
     * @param newPasswd 新密码。
     * @return 修改结果。
     * @throws Exception 修改异常。
     */
    boolean forceModifyPassword(@Nonnull final String userId, @Nonnull final String newPasswd) throws Exception;

    /**
     * 重置密码。
     *
     * @param userId 用户ID
     * @return 重置结果。
     * @throws Exception 异常。
     */
    boolean resetPasswd(@Nonnull final String userId) throws Exception;

    /**
     * 注册账号。
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
    T register(@Nonnull final String account, @Nullable final String passwd, @Nullable final String mobile) throws AuthenException;

}
