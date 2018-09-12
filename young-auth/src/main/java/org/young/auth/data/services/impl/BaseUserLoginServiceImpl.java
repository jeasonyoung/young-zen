package org.young.auth.data.services.impl;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.young.auth.data.services.UserLoginService;
import org.young.auth.util.TokenUtils;
import org.young.common.Status;
import org.young.common.data.dao.BaseUserDao;
import org.young.common.data.dao.BaseUserLoginDao;
import org.young.common.data.domain.BaseDataUser;
import org.young.common.data.domain.BaseDataUserLogin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 登录-数据服务接口实现基类。
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/25 23:07
 */
@Slf4j
public abstract class BaseUserLoginServiceImpl<T extends BaseDataUserLogin> implements UserLoginService<T> {

    /**
     * 获取用户登录数据操作接口。
     * @return 用户登录数据操作接口。
     */
    protected abstract BaseUserLoginDao<T> getUserLoginDao();

    /**
     * 获取用户数据操作接口。
     * @return 用户数据操作接口。
     */
    protected abstract BaseUserDao<? extends BaseDataUser> getUserDao();

    /**
     * 创建用户登录实例对象。
     * @return 用户登录实例对象。
     */
    protected abstract T createUserLoginInstance();

    /**
     * 根据令牌加载登录数据。
     * @param token
     * 令牌。
     * @return 登录数据。
     */
    @Override
    public T loadByToken(@Nonnull final String token) {
        log.debug("loadByToken(token: {})...", token);
        //检查参数
        Assert.hasText(token, "'token'不能为空!");
        //加载数据
        return getUserLoginDao().loadByToken(token);
    }

    /**
     * 根据刷新令牌加载登录数据。
     * @param refreshToken
     * 刷新令牌。
     * @return 登录数据。
     */
    @Override
    public T loadByRefreshToken(@Nonnull final String refreshToken) {
        log.debug("loadByRefreshToken(refreshToken: {})...", refreshToken);
        //检查参数
        Assert.hasText(refreshToken, "'refreshToken'不能为空!");
        //加载数据
        return getUserLoginDao().loadByRefreshToken(refreshToken);
    }

    /**
     * 加载用户最新刷新令牌。
     * @param userId
     * 用户ID。
     * @return 最新刷新令牌。
     */
    @Override
    public String loadLastRefreshTokenByUser(@Nonnull final String userId) {
        log.debug("loadLastRefreshTokenByUser(userId: {})...", userId);
        //检查参数
        Assert.hasText(userId, "'userId'不能为空!");
        //加载用户最新刷新令牌
        return getUserLoginDao().loadLastRefreshTokenByUser(userId);
    }

    /**
     * 更新用户登录状态。
     * @param loginId
     * 用户登录ID。
     * @param status
     * 状态。
     * @return 更新结果。
     */
    @Override
    public boolean updateStatus(@Nonnull final String loginId, @Nonnull final Status status) {
        log.debug("updateStatus(loginId: {}, status: {})...", loginId, status);
        //检查参数
        Assert.hasText(loginId, "'loginId'不能为空!");
        Assert.notNull(status, "'status'不能为空!");
        //加载数据
        final T item = getUserLoginDao().loadById(loginId);
        if(item != null){
            //设置状态
            item.setStatus(status.getVal());
            item.setToken(null);
            item.setIpAddr(null);
            item.setMac(null);
            item.setUserId(null);
            item.setRefreshToken(null);
            item.setCreateTime(null);
            item.setLastTime(null);
            //更新数据
            return getUserLoginDao().update(item) > 0;
        }
        return false;
    }

    /**
     * 更新用户登录令牌。
     * @param loginId
     * 用户登录ID。
     * @param token
     * 用户登录令牌。
     * @return 更新结果。
     */
    @Override
    public boolean updateToken(@Nonnull final String loginId, @Nonnull final String token) {
        log.debug("updateToken(loginId: {}, token: {})...", loginId, token);
        //检查参数
        Assert.hasText(loginId, "'loginId'不能为空!");
        Assert.hasText(token, "'token'不能为空!");
        //加载数据
        final T item = getUserLoginDao().loadById(loginId);
        if(item != null){
            //设置令牌
            item.setToken(token);
            item.setStatus(null);
            item.setIpAddr(null);
            item.setMac(null);
            item.setUserId(null);
            item.setRefreshToken(null);
            item.setCreateTime(null);
            item.setLastTime(null);
            //更新数据
            return getUserLoginDao().update(item) > 0;
        }
        return false;
    }

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
    @Override
    public T createToken(@Nonnull final String userId, @Nullable final String ipAddr, @Nullable final String mac) {
        log.debug("createToken(userId: {}, ipAddr: {}, mac: {})...", userId, ipAddr, mac);
        //检查参数
        Assert.hasText(userId, "'userId'不能为空!");
        //检查用户是否存在
        final boolean has = getUserDao().has(userId);
        if (!has) {
            //用户不存在
            log.warn("createToken-hasById(userId: {})-用户不存在!", userId);
            throw new RuntimeException("用户不存在!");
        }
        //创建登录数据
        final T data = createUserLoginInstance();
        //设置登录ID
        data.createId();
        //设置用户ID
        data.setUserId(userId);
        //设置IPaddr
        if (!Strings.isNullOrEmpty(ipAddr)) {
            data.setIpAddr(ipAddr);
        }
        //设置设备标识
        if (!Strings.isNullOrEmpty(mac)) {
            data.setMac(mac);
        }
        //设置状态
        data.setStatus(Status.Enabled.getVal());
        //设置令牌
        data.setToken(TokenUtils.createNewToken());
        //设置刷新令牌
        data.setRefreshToken(TokenUtils.createNewRefreshToken(data.getId(), data.getToken()));
        //新增保存数据
        final boolean ret = getUserLoginDao().add(data) > 0;
        if (ret) {
            log.info("createToken(userId: {}, ipAddr: {}, mac: {})-创建登录令牌成功!", userId, ipAddr, mac);
            return data;
        }
        return null;
    }

    /**
     * 移除此次登录令牌。
     * @param loginId
     * 登录ID。
     * @return 移除结果。
     */
    @Override
    public boolean removeTokenByLogin(@Nonnull final String loginId) {
        log.debug("removeTokenByLogin(loginId: {})...", loginId);
        //检查参数
        Assert.hasText(loginId, "'userId'不能为空!");
        //更新数据
        return getUserLoginDao().updateStatusByLogin(loginId, Status.Delete.getVal()) > 0;
    }

    /**
     * 移除用户全部登录令牌。
     * @param userId
     * 用户ID。
     * @return 移除结果。
     */
    @Override
    public boolean removeAllTokenByUser(@Nonnull final String userId) {
        log.debug("removeAllTokenByUser(userId: {})...", userId);
        //检查参数
        Assert.hasText(userId, "'userId'不能为空!");
        //更新令牌状态
        return getUserLoginDao().updateAllStatusByUser(userId, Status.Delete.getVal()) > 0;
    }
}
