package org.young.auth.services.impl;

import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.young.auth.data.services.UserLoginService;
import org.young.auth.data.services.UserService;
import org.young.auth.model.UserAccount;
import org.young.auth.model.UserCertificate;
import org.young.auth.model.UserInfo;
import org.young.auth.model.UserVipInfo;
import org.young.auth.services.UserAuthenService;
import org.young.auth.util.TokenUtils;
import org.young.common.Status;
import org.young.common.data.domain.BaseDataUser;
import org.young.common.data.domain.BaseDataUserLogin;
import org.young.common.exception.AuthenException;
import org.young.common.exception.TokenException;
import org.young.common.protocol.provider.TokenUser;
import org.young.common.protocol.provider.TokenUserData;
import org.young.common.util.NetUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

/**
 * 用户-认证服务接口基类。
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/27 09:25
 */
@Slf4j
public abstract class BaseUserAuthenServiceImpl implements UserAuthenService {
    /**
     * 令牌到期时间间隔(7200秒)
     */
    private static final long TOKEN_EXPIRE_INTERVAL = 7200 * 1000;

    /**
     * 获取用户 数据服务接口
     * @return 数据服务接口
     */
    protected abstract UserService<? extends BaseDataUser> getUserService();

    /**
     * 获取用户登录 数据服务接口
     * @return 数据服务接口
     */
    protected abstract UserLoginService<? extends BaseDataUserLogin> getUserLoginService();

    /**
     * 获取是否校验用户密码。
     * @return 是否校验用户密码。
     */
    protected abstract boolean hasVerifyUserPassword();

    /**
     * 用户认证。
     * @param account
     * 登录账号。
     * @param password
     * 登录密码。
     * @param mac
     * 设备标识。
     * @param validId
     * 验证码ID。
     * @param validCode
     * 验证码代码。
     * @return 认证结果。
     * @throws AuthenException
     * 认证异常。
     */
    @Override
    public UserCertificate authen(@Nonnull final String account, @Nullable final String password, @Nullable final String mac, @Nullable final String validId, @Nullable final String validCode) throws AuthenException {
        log.debug("authen(account: {}, password: {}, mac: {}, validId: {}, validCode: {})...", account, password, mac, validId, validCode);
        //检查参数
        if(Strings.isNullOrEmpty(account)){
            log.warn("authen(account: {})-账号为空!", account);
            throw new AuthenException.AuthenAccountBlankException();
        }
        //是否需要校验用户密码
        final boolean hasVerifyPwd = hasVerifyUserPassword();
        if(hasVerifyPwd && Strings.isNullOrEmpty(password)){
            log.warn("authen(account: {})-密码为空!", account);
            throw new AuthenException.AuthenPasswordBlankException();
        }
        //检查是否需要校验验证码
        if(!Strings.isNullOrEmpty(validId) && !Strings.isNullOrEmpty(validCode)) {
            //校验验证码
            final boolean ret = verifyValidCode(validId, validCode);
            if(!ret){
                log.warn("authen-verifyValidCode(validId: {}, validCode:{})-验证码错误", validId, validCode);
                throw new AuthenException.AuthenValidCodeException();
            }
        }
        //加载用户数据
        final BaseDataUser user = getUserService().loadUserByAccount(account);
        if(user == null){
            log.warn("authen(account: {})-账号不存在!", account);
            throw new AuthenException.AuthenAccountNotExistException();
        }
        //检查账号状态
        final Status status = Status.parse(user.getStatus());
        if(status != Status.Enabled){
            log.warn("authen(account: {})-账号被禁用!", account);
            throw new AuthenException.AuthenAccountDisableException();
        }
        //是否校验密码
        if(hasVerifyPwd && !Strings.isNullOrEmpty(password)) {
            //校验用户登录密码
            final boolean ret = getUserService().verifyPassword(user.getId(), password);
            if (!ret) {
                log.warn("authen-verifyPassword(userId: {}, password: {})-密码错误!", user.getId(), password);
                throw new AuthenException.AuthenPasswordErrorException();
            }
        }
        final String ipAddr = loadCurrentIpAddress();
        //创建用户登录令牌数据
        final BaseDataUserLogin userLogin = getUserLoginService().createToken(user.getId(), ipAddr, mac);
        if(userLogin == null){
            log.error("authen-createToken(userId: {}, ipAddr: {}, mac: {})-创建用户登录令牌失败!", user.getId(), ipAddr, mac);
            throw new AuthenException("创建用户登录令牌失败!");
        }
        //创建认证结果数据
        return createCertificate(user, userLogin);
    }

    /**
     * 校验验证码。
     * @param validId
     * 验证码ID。
     * @param validCode
     * 验证码代码。
     * @return 校验结果。
     */
    protected abstract boolean verifyValidCode(@Nullable final String validId,@Nullable final String validCode);

    /**
     * 获取当前IP地址
     * @return IP地址。
     */
    private String loadCurrentIpAddress() {
        log.debug("loadCurrentIpAddress...");
        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(requestAttributes != null) {
            return NetUtils.getIpAddr(requestAttributes.getRequest());
        }
        return null;
    }

    /**
     * 创建用户认证结果数据。
     * @param user
     * 用户数据。
     * @param userLogin
     * 用户登录数据。
     * @param <U>
     *     用户数据类型。
     * @param <UL>
     *     用户登录数据类型。
     * @return
     * 用户认证结果数据。
     */
    private <U extends BaseDataUser, UL extends BaseDataUserLogin> UserCertificate createCertificate(@Nonnull final U user, @Nonnull final UL userLogin){
        log.debug("createCertificate(user: {}, userLogin: {})...", user, userLogin);
        final UserCertificate row = new UserCertificate();
        //设置令牌
        row.setToken(userLogin.getToken());
        //设置刷新令牌
        row.setRefreshToken(userLogin.getRefreshToken());
        //设置用户信息
        row.setUser(buildUserInfo(user));
        //
        return row;
    }

    /**
     * 构建用户信息
     * @param user
     * 用户数据。
     * @param <U>
     *     用户数据类型。
     * @return 用户信息。
     */
    private <U extends BaseDataUser> UserInfo buildUserInfo(@Nonnull final U user){
        log.debug("buildUserInfo(user: {})...", user);
        final UserInfo info = new UserInfo();
        //设置用户ID
        info.setId(user.getId());
        //设置用户姓名
        info.setName(user.getName());
        //设置用户头像URL
        info.setAvatar(user.getAvatar());
        //设置VIP信息
        info.setVip(loadVipInfo(user.getId()));
        return info;
    }

    /**
     * 加载用户VIP信息。
     * @param userId
     * 用户ID。
     * @return VIP信息。
     */
    protected abstract UserVipInfo loadVipInfo(@Nonnull final String userId);

    /**
     * 退出登录。
     * @param userId
     * 用户ID。
     * @return 退出结果。
     */
    @Override
    public boolean logout(@Nonnull final String userId) {
        log.debug("logout(userId: {})...", userId);
        //检查参数
        Assert.hasText(userId, "'token'不能为空!");
        //移除用户全部登录令牌
        return getUserLoginService().removeAllTokenByUser(userId);
    }

    /**
     * 用户修改密码。
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
    @Override
    public boolean modifyPassword(@Nonnull final String userId, @Nonnull final String oldPasswd, @Nonnull final String newPasswd) throws Exception {
        log.debug("modifyPassword(userId: {}, oldPasswd: {}, newPasswd: {})...", userId, oldPasswd, newPasswd);
        //检查参数
        Assert.hasText(userId, "'userId'不能为空!");
        Assert.hasText(oldPasswd, "'oldPasswd'不能为空!");
        Assert.hasText(newPasswd, "'newPasswd'不能为空!");
        //修改密码处理
        return getUserService().modifyPassword(userId, oldPasswd, newPasswd);
    }

    /**
     * 强制修改密码。
     * @param userId
     * 用户ID。
     * @param newPasswd
     * 新密码。
     * @return 修改结果。
     * @throws Exception
     * 修改异常。
     */
    @Override
    public boolean forceModifyPassword(@Nonnull final String userId, @Nonnull final String newPasswd) throws Exception {
        log.debug("forceModifyPassword(userId: {}, newPasswd: {})...", userId, newPasswd);
        //检查参数
        Assert.hasText(userId, "'userId'不能为空!");
        Assert.hasText(newPasswd, "'newPasswd'不能为空!");
        //强制修改密码处理
        return getUserService().forceModifyPassword(userId, newPasswd);
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
        //重置密码处理
        return getUserService().resetPasswd(userId);
    }

    /**
     * 注册账号。
     * @param account
     * 账号。
     * @param passwd
     * 密码。
     * @param mobile
     * 手机号码。
     * @return 注册结果。
     * @throws AuthenException
     * 注册异常。
     */
    @Override
    public UserAccount register(@Nonnull final String account, @Nullable final String passwd, @Nullable final String mobile) throws AuthenException {
        log.debug("register(account: {}, passwd: {})...", account, passwd);
        //检查参数
        Assert.hasText(account, "'account'不能为空!");
        //注册账号处理
        final BaseDataUser item = getUserService().register(account, passwd, mobile);
        if(item != null){
            //初始化用户账号
            final UserAccount row = new UserAccount();
            //设置用户ID
            row.setUserId(item.getId());
            //设置用户账号
            row.setAccount(item.getAccount());
            //返回
            return row;
        }
        return null;
    }
    /**
     * 加载用户数据。
     * @param userId
     * 用户ID。
     * @return 用户数据。
     */
    @Override
    public UserInfo loadUserById(@Nonnull final String userId) {
        log.debug("loadUserById(userId: {})...", userId);
        //检查参数
        Assert.hasText(userId, "'userId'不能为空!");
        //加载用户数据
        final BaseDataUser user = getUserService().loadUserById(userId);
        if(user == null){
            log.warn("loadUserById(userId: {})-用户不存在!", userId);
            return null;
        }
        return buildUserInfo(user);
    }

    /**
     * 令牌用户数据格式。
     * @param item
     * 用户登录数据。
     * @return 令牌用户数据。
     */
    private static TokenUserData convert(@Nullable final BaseDataUserLogin item){
        if(item != null) {
            //初始化令牌数据
            final TokenUserData row = new TokenUserData();
            //设置登录ID
            row.setLoginId(item.getId());
            //设置用户ID
            row.setUserId(item.getUserId());
            //设置令牌
            row.setToken(item.getToken());
            //设置刷新令牌
            row.setRefreshToken(item.getRefreshToken());
            //返回
            return row;
        }
        return null;
    }

    /**
     * 根据令牌加载用户数据。
     * @param token
     * 登录令牌。
     * @return 用户数据。
     * @throws TokenException
     * 令牌异常。
     */
    @Override
    public TokenUser loadUserByToken(@Nonnull final String token) throws TokenException {
        log.debug("loadUserByToken(token: {})...", token);
        if(Strings.isNullOrEmpty(token)){
            throw new TokenException("令牌为空!");
        }
        final BaseDataUserLogin item = getUserLoginService().loadByToken(token);
        if(item == null){
            log.warn("loadUserByToken-loadByToken(token: {}):令牌不存在!", token);
            throw new TokenException("令牌不存在!");
        }
        //获取令牌状态
        Status status = Status.parse(item.getStatus());
        if(status == Status.Delete){
            log.warn("loadUserByToken(token: {}, status: {})-用户已退出登录!", token, status);
            throw new TokenException("用户已退出!");
        }
        //检查是否过期
        if(status == Status.Disabled){
            log.warn("令牌[item: {}]-status: {} 已过期！", item, status);
            throw new TokenException.TokenExpireException();
        }
        //检查最后更新时间
        if(item.getLastTime() != null) {
            //检查令牌是否过期
            final long interval = System.currentTimeMillis() - item.getLastTime().getTime();
            if (interval >= TOKEN_EXPIRE_INTERVAL) {
                status = Status.Disabled;
                //令牌过期重置令牌状态
                item.setStatus(status.getVal());
                //更新数据
                final boolean ret = getUserLoginService().updateStatus(item.getId(), status);
                log.info("convert-updateStatus(token: {}, ttl: {})-已被标识为过期-ret: {}", item.getToken(), interval, ret);
                throw new TokenException.TokenExpireException();
            }
        }
        //反馈数据转换
        return convert(item);
    }

    /**
     * 刷新令牌缓存
     */
    private final Cache<String, Long> refreshTokenCache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

    /**
     * 根据刷新令牌加载登录数据。
     * @param refreshToken
     * 刷新令牌。
     * @return 登录数据。
     */
    @Override
    public TokenUserData loadUserByRefreshToken(@Nonnull final String refreshToken) throws TokenException {
        log.debug("loadUserByRefreshToken(refreshToken: {})...", refreshToken);
        //检查参数
        if(Strings.isNullOrEmpty(refreshToken)){
            throw new TokenException.TokenRefreshInvalidException("刷新令牌为空!");
        }
        BaseDataUserLogin item;
        //检查是否被缓存
        final Long exp = refreshTokenCache.getIfPresent(refreshToken);
        if(exp == null){
            synchronized (this) {
                refreshTokenCache.put(refreshToken, System.currentTimeMillis());
            }
            item = buildRefreshTokenInner(refreshToken);
        }else {
            final long duration = 500;
            if(System.currentTimeMillis() - exp <= duration){
                try {
                    //线程等待
                    Thread.sleep(duration);
                }catch (Throwable ex){
                    log.warn("loadUserByRefreshToken(refreshToken:"+ refreshToken +", duration: "+ duration +")-线程等待异常:" + ex.getMessage());
                }
            }
            item = buildSyncRefreshTokenInner(refreshToken);
        }
        //反馈数据转换
        return convert(item);
    }

    /**
     * 构建刷新令牌内部实现
     * @param refreshToken
     * 刷新令牌
     * @return 刷新后数据
     * @throws TokenException
     * 刷新异常
     */
    private BaseDataUserLogin buildRefreshTokenInner(@Nonnull final String refreshToken) throws TokenException{
        log.debug("buildRefreshToken(refreshToken: {})...", refreshToken);
        //根据刷新令牌加载数据
        final BaseDataUserLogin item = getUserLoginService().loadByRefreshToken(refreshToken);
        if (item == null) {
            log.warn("loadUserByRefreshToken-loadByRefreshToken(refreshToken: {}):刷新令牌不存在!", refreshToken);
            throw new TokenException.TokenRefreshInvalidException("刷新令牌不存在!");
        }
        //加载令牌状态
        final Status status = Status.parse(item.getStatus());
        if (status == Status.Delete) {
            log.warn("loadUserByRefreshToken(refreshToken: {}, status: {})-用户已退出登录!", refreshToken, status);
            throw new TokenException.TokenRefreshInvalidException();
        }
        if (item.getLastTime() != null) {
            //检查令牌是否过期
            final long interval = System.currentTimeMillis() - item.getLastTime().getTime();
            //令牌被标识为过期或令牌时间过期
            if ((status == Status.Disabled) || (interval >= TOKEN_EXPIRE_INTERVAL)) {
                //令牌过期生成新令牌
                final String newToken = TokenUtils.createNewToken();
                log.info("loadUserByRefreshToken(refreshToken: {}, ttl: {}, token: {})-令牌已过期将更新令牌[newToken: {}]!", refreshToken, interval, item.getToken(), newToken);
                //更新令牌
                item.setToken(newToken);
                //更新令牌
                boolean ret = getUserLoginService().updateToken(item.getId(), newToken);
                log.info("loadUserByRefreshToken-updateToken(data: {}, ttl: {})-ret: {}", item, interval, ret);
                //更新状态
                if (ret && status != Status.Enabled) {
                    final Status upStatus = Status.Enabled;
                    item.setStatus(upStatus.getVal());
                    //更新数据
                    ret = getUserLoginService().updateStatus(item.getId(), upStatus);
                    log.info("loadUserByRefreshToken-updateStatus(item: {}, ttl: {})-ret: {}", item, interval, ret);
                }
            }
        }
        return item;
    }

    /**
     * 构建同步刷新令牌内部实现
     * @param refreshToken
     * 刷新令牌
     * @return 刷新后数据
     * @throws TokenException
     * 刷新异常
     */
    private synchronized BaseDataUserLogin buildSyncRefreshTokenInner(@Nonnull final String refreshToken) throws TokenException {
        log.debug("buildSyncRefreshTokenInner(refreshToken: {})...", refreshToken);
        return buildRefreshTokenInner(refreshToken);
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
        //加载数据
        return getUserLoginService().loadLastRefreshTokenByUser(userId);
    }
}
