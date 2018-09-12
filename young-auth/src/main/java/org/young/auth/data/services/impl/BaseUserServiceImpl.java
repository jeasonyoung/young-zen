package org.young.auth.data.services.impl;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.young.auth.data.services.UserService;
import org.young.common.Status;
import org.young.common.data.dao.BaseUserDao;
import org.young.common.data.domain.BaseDataUser;
import org.young.common.data.services.impl.BaseDataService;
import org.young.common.exception.AuthenException;
import org.young.common.util.EncryptUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 用户-数据服务基类
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/26 17:01
 */
@Slf4j
public abstract class BaseUserServiceImpl<T extends BaseDataUser> extends BaseDataService implements UserService<T> {

    /**
     * 获取用户数据操作
     * @return 数据操作。
     */
    protected abstract BaseUserDao<T> getUserDao();

    /**
     * 加载用户数据.
     * @param userId
     * 用户ID。
     * @return 用户数据。
     */
    @Override
    public T loadUserById(@Nonnull final String userId){
        log.debug("loadUserById(userId: {})...", userId);
        //检查参数
        Assert.hasText(userId, "'userId'不能为空!");
        //加载数据
        return getUserDao().loadById(userId);
    }

    /**
     * 根据用户账号加载数据。
     *
     * @param account 用户账号。
     * @return 用户数据。
     */
    @Override
    public T loadUserByAccount(@Nonnull final String account) {
        log.debug("loadUserByAccount(account: {})...", account);
        //检查参数
        Assert.hasText(account, "'account'不能为空!");
        //加载数据
        return getUserDao().loadByAccount(account);
    }

    /**
     * 验证密码。
     * @param userId
     * 用户ID。
     * @param password
     * 密码。
     * @return 验证结果。
     */
    @Override
    public boolean verifyPassword(@Nonnull final String userId,@Nonnull final String password) throws AuthenException {
        log.debug("verifyPassword(userId: {}, password: {})...", userId, password);
        //检查参数
        Assert.hasText(userId, "'userId'不能为空!");
        if(Strings.isNullOrEmpty(password)){
            log.warn("verifyPassword(userId: {}, password: {})-密码为空!", userId, password);
            throw new AuthenException.AuthenPasswordBlankException();
        }
        //加载用户数据
        final T data = loadUserById(userId);
        if(data == null){
            log.warn("verifyPassword(userId: {})-用户不存在!", userId);
            throw new AuthenException("用户不存在!");
        }
        //加密密码
        final String encPwd = EncryptUtils.createEncryptPasswd(userId, password);
        //校验密码
        final boolean ret = encPwd.equalsIgnoreCase(data.getPassword());
        log.info("verifyPassword(userId: {}, password: {})[encPwd: {}, dataPwd: {}]=>ret: {}", userId, password, encPwd, data.getPassword(), ret);
        return ret;
    }

    /**
     * 用户修改密码。
     * @param userId
     * 用户ID。
     * @param oldPasswd
     * 旧密码。
     * @param newPasswd
     * 新密码。
     * @throws Exception
     * 修改异常。
     */
    @Override
    public boolean modifyPassword(@Nonnull final String userId, @Nonnull final String oldPasswd, @Nonnull final String newPasswd) throws Exception {
        log.debug("modifyPassword(userId: {}, oldPasswd: {}, newPasswd: {})...", userId, oldPasswd, newPasswd);
        //检查参数
        Assert.hasText(userId, "'userId'不能为空!");
        Assert.hasText(oldPasswd, "'oldPasswd'不能为空!");
        Assert.hasText(newPasswd, "'newPasswd'不能为空!");
        //加载用户数据
        final T item = getUserDao().loadById(userId);
        if(item == null){
            log.warn("modifyPassword(userId: {})-用户不存在!", userId);
            throw new Exception("用户不存在!");
        }
        //加密旧密码
        final String encOld = EncryptUtils.createEncryptPasswd(userId, oldPasswd);
        //校验旧密码
        if(!encOld.equalsIgnoreCase(item.getPassword())){
            log.warn("modifyPassword(userId: {})-旧密码不正确!", userId);
            throw new Exception("旧密码不正确!");
        }
        //设置新密码
        item.setPassword(EncryptUtils.createEncryptPasswd(userId, newPasswd));
        //重置其它值
        item.setName(null);
        item.setAvatar(null);
        item.setAccount(null);
        item.setEmail(null);
        item.setMobile(null);
        item.setStatus(null);
        //更新数据
        return getUserDao().update(item) > 0;
    }

    /**
     * 强制修改密码。
     *
     * @param userId    用户ID。
     * @param newPasswd 新密码。
     * @return 修改结果。
     * @throws Exception 修改异常。
     */
    @Override
    public boolean forceModifyPassword(@Nonnull final String userId, @Nonnull final String newPasswd) throws Exception {
        log.debug("forceModifyPassword(userId: {}, newPasswd: {})...", userId, newPasswd);
        //检查参数
        Assert.hasText(userId, "'userId'不能为空!");
        Assert.hasText(newPasswd, "'newPasswd'不能为空!");
        //加载用户数据
        final T item = getUserDao().loadById(userId);
        if(item == null){
            log.warn("forceModifyPassword(userId: {})-用户不存在!", userId);
            throw new Exception("用户不存在!");
        }
        //重置密码
        item.setPassword(EncryptUtils.createEncryptPasswd(userId, newPasswd));
        //重置其它值
        item.setName(null);
        item.setAvatar(null);
        item.setAccount(null);
        item.setEmail(null);
        item.setMobile(null);
        item.setStatus(null);
        //更新数据
        return getUserDao().update(item) > 0;
    }

    /**
     * 重置密码。
     * @param userId
     * 用户ID
     * @return 重置结果。
     * @throws Exception
     * 异常。
     */
    @Override
    public boolean resetPasswd(@Nonnull final String userId) throws Exception {
        log.debug("resetPasswd(userId: {})...", userId);
        //检查参数
        Assert.hasText(userId, "'userId'不能为空!");
        //加载用户数据
        final T item = getUserDao().loadById(userId);
        if(item == null){
            log.warn("resetPasswd(userId: {})-用户不存在!", userId);
            throw new Exception("用户不存在!");
        }
        //设置重置密码
        item.setPassword(EncryptUtils.createEncryptDefaultPasswd(userId));
        //重置其它值
        item.setName(null);
        item.setAvatar(null);
        item.setAccount(null);
        item.setEmail(null);
        item.setMobile(null);
        item.setStatus(null);
        //更新数据
        return getUserDao().update(item) > 0;
    }

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
    @Override
    public T register(@Nonnull final String account, @Nullable final String passwd, @Nullable final String mobile) throws AuthenException {
        log.debug("register(account: {}, passwd: {})...", account, passwd);
        //检查参数
        if(Strings.isNullOrEmpty(account)){
            log.warn("register(account: {})-账号为空!", account);
            throw new AuthenException.AuthenAccountBlankException();
        }
        //检查账号是否存在
        T data = getUserDao().loadByAccount(account);
        if(data != null){
            log.warn("register(account: {})-账号已存在!", account);
            return data;
        }
        //初始化对象
        data = createNewInstance();
        //设置用户ID
        data.createId();
        //设置账号
        data.setAccount(account);
        //设置用户名
        data.setName(account);
        //设置访问密码
        if(!Strings.isNullOrEmpty(passwd)) {
            data.setPassword(EncryptUtils.createEncryptPasswd(data.getId(), passwd));
        }
        //设置用户手机号码
        if(!Strings.isNullOrEmpty(mobile)) {
            data.setMobile(mobile);
        }
        //设置状态
        data.setStatus(Status.Enabled.getVal());
        //新增数据
        if(getUserDao().add(data) > 0){
            return data;
        }
        return null;
    }

    /**
     * 创建新实例。
     * @return 对象实例。
     */
    protected abstract T createNewInstance();
}
