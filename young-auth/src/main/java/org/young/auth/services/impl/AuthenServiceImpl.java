package org.young.auth.services.impl;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.young.auth.event.AuthenEvent;
import org.young.auth.model.UserAccount;
import org.young.auth.model.UserCertificate;
import org.young.auth.model.UserInfo;
import org.young.auth.services.AuthenService;
import org.young.auth.services.ChannelAuthenBeansService;
import org.young.auth.services.UserAuthenService;
import org.young.common.Callback;
import org.young.common.exception.AuthenException;
import org.young.common.exception.TokenException;
import org.young.common.protocol.provider.TokenUser;
import org.young.common.protocol.provider.TokenUserData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * 认证-服务接口实现
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/26 14:43
 */
@Slf4j
@Service
public class AuthenServiceImpl implements AuthenService {

    /**
     * 注入-Spring上下文
     */
    @Autowired
    private ApplicationContext context = null;

    /**
     * 注入-渠道用户认证服务
     */
    @Autowired
    private ChannelAuthenBeansService authenService = null;

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
    @Override
    public UserCertificate authen(@Nonnull final Integer channel, @Nonnull final String account, @Nullable final String password, @Nullable final String mac, @Nullable final String validId, @Nullable final String validCode) throws AuthenException {
        log.debug("authen(channel: {}, account: {}, password: {}, mac: {}, validId: {}, validCode: {})...", channel, account, password, mac, validId, validCode);
        //检查参数
        Assert.notNull(channel, "'channel'不能为空");
        Assert.hasText(account, "'account'不能为空!");
        //
        try {
            return userAuthenServiceHandler(channel, service -> {
                //用户认证
                final UserCertificate ret = service.authen(account, password, mac, validId, validCode);
                if (ret != null) {
                    //是否触发事件
                    if (context != null && ret.getUser() != null) {
                        //初始化事件对象
                        final AuthenEvent event = new AuthenEvent(this, AuthenEvent.AuthenEventType.Login);
                        //设置渠道号
                        event.setChannel(channel);
                        //设置用户ID
                        event.setUserId(ret.getUser().getId());
                        //触发事件
                        context.publishEvent(event);
                    }
                    return ret;
                }
                return null;
            });
        }catch (AuthenException ex){
            log.warn("authen-userAuthenServiceHandler(channel: "+ channel +")-exp:" + ex.getMessage());
            throw ex;
        }catch (Throwable ex){
            throw new AuthenException(ex.getMessage());
        }
    }

    /**
     * 退出登录。
     * @param channel
     * 渠道号。
     * @param userId
     * 用户ID。
     * @return 退出结果。
     */
    @Override
    public boolean logout(@Nonnull final Integer channel, @Nonnull final String userId) {
        log.debug("logout(channel: {}, token: {})...", channel, userId);
        //检查参数
        Assert.notNull(channel, "'channel'不能为空");
        Assert.notNull(userId, "'userId'不能为空");
        try {
            return userAuthenServiceHandler(channel, service -> {
                //注销登录
                final boolean ret = service.logout(userId);
                if (ret && context != null) {
                    //初始化事件对象
                    final AuthenEvent event = new AuthenEvent(this, AuthenEvent.AuthenEventType.Logout);
                    //设置渠道号
                    event.setChannel(channel);
                    //设置用户ID
                    event.setUserId(userId);
                    //触发事件
                    context.publishEvent(event);
                }
                return ret;
            });
        }catch (Throwable ex){
            log.warn("logout(channel: {}, userId: {})-exp: {}", channel, userId, ex);
        }
        return false;
    }

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
    @Override
    public boolean modifyPassword(@Nonnull final Integer channel, @Nonnull final String userId, @Nonnull final String oldPasswd, @Nonnull final String newPasswd) throws Exception {
        log.debug("modifyPassword(channel: {}, userId: {}, oldPasswd: {}, newPasswd: {})...", channel, userId, oldPasswd, newPasswd);
        //检查参数
        Assert.notNull(channel, "'channel'不能为空");
        Assert.hasText(userId, "'userId'不能为空!");
        Assert.hasText(oldPasswd, "'oldPasswd'不能为空!");
        Assert.hasText(newPasswd, "'newPasswd'不能为空!");
        //
        return userAuthenServiceHandler(channel, service -> service.modifyPassword(userId, oldPasswd, newPasswd));
    }

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
    @Override
    public boolean forceModifyPassword(@Nonnull final Integer channel, @Nonnull final String userId, @Nonnull final String newPasswd) throws Exception {
        log.debug("forceModifyPassword(channel: {}, userId:{}, newPasswd: {})...", channel, userId, newPasswd);
        //检查参数
        Assert.notNull(channel, "'channel'不能为空");
        Assert.hasText(userId, "'userId'不能为空!");
        Assert.hasText(newPasswd, "'newPasswd'不能为空!");
        //
        return userAuthenServiceHandler(channel, service -> service.forceModifyPassword(userId, newPasswd));
    }

    /**
     * 注册账号。
     * @param channel
     * 渠道号。
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
    public UserAccount register(@Nonnull final Integer channel, @Nonnull final String account, @Nullable final String passwd, @Nullable final String mobile) throws AuthenException {
        log.debug("register(channel: {}, account: {}, passwd: {})...", channel, account, passwd);
        //检查参数
        Assert.notNull(channel, "'channel'不能为空");
        if(Strings.isNullOrEmpty(account)) {
            throw new AuthenException.AuthenAccountBlankException();
        }
        try {
            return userAuthenServiceHandler(channel, service -> service.register(account, passwd, mobile));
        }catch (AuthenException ex){
            throw ex;
        }catch (Throwable ex){
            throw new AuthenException(ex.getMessage());
        }
    }

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
    @Override
    public boolean resetPasswd(@Nonnull final Integer channel, @Nonnull final String userId) throws Exception {
        log.debug("resetPasswd(channel: {}, userId: {})...", channel, userId);
        //检查参数
        Assert.notNull(channel, "'channel'不能为空");
        Assert.hasText(userId, "'userId'不能为空!");
        //
        return userAuthenServiceHandler(channel, service -> service.resetPasswd(userId));
    }

    /**
     * 加载用户数据.
     * @param channel
     * 渠道号。
     * @param userId
     * 用户ID。
     * @return 用户数据。
     */
    @Override
    public UserInfo loadUserById(@Nonnull final Integer channel, @Nonnull final String userId) {
        log.debug("loadUserById(channel: {}, userId: {})...", channel, userId);
        //检查参数
        Assert.notNull(channel, "'channel'不能为空");
        Assert.hasText(userId, "'userId'不能为空!");
        try {
            //
            return userAuthenServiceHandler(channel, service -> service.loadUserById(userId));
        }catch (Throwable ex){
            log.warn("loadUserById(channel: {}, userId: {})-exp: {}", channel, userId, ex);
        }
        return null;
    }

    /**
     * 根据渠道令牌加载用户数据。
     * @param channel
     * 渠道号。
     * @param token
     * 登录令牌。
     * @return 用户数据。
     */
    @Override
    public Callback<TokenUser> loadUserByToken(@Nonnull final Integer channel, @Nonnull final String token) {
        log.debug("loadUserByToken(channel: {}, token: {})...", channel, token);
        final Callback<TokenUser> callback = new Callback<>();
        try {
            //检查参数
            Assert.notNull(channel, "'channel'不能为空");
            //加载数据
            final TokenUser tokenUser = userAuthenServiceHandler(channel, service -> service.loadUserByToken(token));
            if(tokenUser != null){
                callback.setData(tokenUser);
            }
        } catch (TokenException ex){
            log.warn("loadUserByToken(channel: "+ channel +", token: "+ token +")-exp:" + ex.getMessage(), ex);
            callback.setCode(ex.getCode());
            callback.setMessage(ex.getMessage());
        }catch (Throwable ex){
            log.warn("loadUserByToken(channel: "+ channel +", token: "+ token +")-exp:" + ex.getMessage(), ex);
            callback.setCode(TokenException.code);
            callback.setMessage(ex.getMessage());
        }
        return callback;
    }

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
    @Override
    public TokenUserData loadUserByRefreshToken(@Nonnull final Integer channel, @Nonnull final String refreshToken) throws TokenException {
        log.debug("loadUserByRefreshToken(channel: {}, refreshToken: {})...", channel, refreshToken);
        //检查参数
        Assert.notNull(channel, "'channel'不能为空");
        if(Strings.isNullOrEmpty(refreshToken)){
            throw new TokenException("刷新令牌为空!");
        }
        try {
            //
            return userAuthenServiceHandler(channel, service -> service.loadUserByRefreshToken(refreshToken));
        }catch (TokenException ex){
            throw ex;
        }catch (Throwable ex){
            throw new TokenException(ex.getMessage());
        }
    }

    /**
     * 加载渠道用户最新刷新令牌。
     * @param channel
     * 渠道号。
     * @param userId
     * 用户ID。
     * @return 最新刷新令牌。
     */
    @Override
    public String loadLastRefreshTokenByUser(@Nonnull final Integer channel, @Nonnull final String userId) {
        log.debug("loadLastRefreshTokenByUser(channel: {}, userId: {})...", channel, userId);
        //检查参数
        Assert.notNull(channel, "'channel'不能为空!");
        Assert.hasText(userId, "'userId'不能为空!");
        try {
            //
            return userAuthenServiceHandler(channel, service -> service.loadLastRefreshTokenByUser(userId));
        }catch (Throwable ex){
            log.warn("loadLastRefreshTokenByUser(channel: {}, userId: {})-exp: {}", channel, userId, ex);
        }
        return null;
    }

    /**
     * 渠道用户认证处理。
     * @param channel
     * 渠道号。
     * @param listener
     * 业务处理监听器。
     */
    private <T> T userAuthenServiceHandler(@Nonnull final Integer channel,@Nonnull final UserAuthenServiceListener<T> listener) throws Exception{
        log.debug("userAuthenServiceHandler(channel: {}, listener: {})....", channel, listener);
        //加载渠道下处理器
        final List<UserAuthenService> services = authenService.loadServiceByChannel(channel);
        //检查处理器
        Assert.notEmpty(services, "'userAuthenServiceHandler'渠道(channel: '"+ channel +"')-未配置处理器!");
        //
        Throwable exp = null;
        T result = null;
        //循环处理
        for(UserAuthenService service : services){
            if(service == null){
                continue;
            }
            try {
                result = listener.handler(service);
                if (result != null) {
                    log.info("userAuthenServiceHandler(channel: {})-handler(service: {})-处理成功退出循环!", channel, service);
                    break;
                }
            }catch (TokenException.TokenExpireException e){
                log.warn("userAuthenServiceHandler(channel:"+channel+")-exp:" + e.getMessage(), e);
                throw e;
            }catch (Throwable e){
                log.warn("userAuthenServiceHandler(channel:"+channel+")-exp:" + e.getMessage(), e);
                exp = e;
            }
        }
        //检查是否应抛出异常
        if(result == null && exp != null){
            if(exp instanceof Exception){
                throw (Exception)exp;
            }
            //抛出异常
            throw new Exception(exp);
        }
        return result;
    }


    /**
     * 用户认证处理监听器。
     * @param <T>
     *     处理结果类型
     */
    private interface UserAuthenServiceListener<T> {

        /**
         * 处理器。
         * @param service
         * 用户认证服务接口。
         * @return 处理结果。
         * @throws Exception
         * 处理异常
         */
        T handler(@Nonnull UserAuthenService service) throws Exception;
    }
}
