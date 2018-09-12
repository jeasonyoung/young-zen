package org.young.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.young.auth.controller.request.LoginRequest;
import org.young.auth.controller.request.RefreshRequest;
import org.young.auth.controller.request.ReqLogoutBody;
import org.young.auth.controller.request.SmsRequest;
import org.young.auth.controller.response.RespLoginBody;
import org.young.auth.controller.response.RespLogoutBody;
import org.young.auth.controller.response.RespRefreshBody;
import org.young.auth.controller.response.RespSmsBody;
import org.young.auth.model.UserCertificate;
import org.young.auth.services.AuthenService;
import org.young.auth.services.SmsValidService;
import org.young.common.Status;
import org.young.common.exception.AuthenException;
import org.young.common.protocol.BaseController;
import org.young.common.protocol.RespStatus;
import org.young.common.protocol.provider.TokenUser;
import org.young.common.protocol.provider.TokenUserData;
import org.young.common.protocol.request.Request;
import org.young.common.protocol.response.Response;

import javax.servlet.http.HttpSession;

/**
 * 用户认证-API
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/9/12 09:08
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthenController extends BaseController {

    /**
     * 注入-短信验证-服务接口
     */
    @Autowired
    private SmsValidService validService = null;

    /**
     * 注入-用户认证-服务接口
     */
    @Autowired
    private AuthenService authenService = null;

    /**
     * 发送短信验证码 API
     * @param request
     * 请求报文
     * @return 响应报文
     */
    @PostMapping("/login/sms")
    public Response<RespSmsBody> sendSms(@RequestBody @Validated final SmsRequest request){
        log.debug("sendSms(request: {})...", request);
        return action(request, (reqHead, reqBody) -> {
            //检查参数
            Assert.hasText(reqBody.getMobile(), "'mobile'不能为空!");
            //渠道号
            final Integer channel = reqHead.getChannel();
            //手机号码
            final String mobile = reqBody.getMobile();
            //用户标识
            final String userSign = reqHead.getToken();
            //初始化响应报文体
            final RespSmsBody respBody = new RespSmsBody();
            try{
                //发送短信验证码
                final SmsValidService.SmsResult ret = validService.sendSmsValid(channel, mobile, userSign);
                if(ret != null && ret.getStatus() == Status.Enabled){
                    //设置验证码ID
                    respBody.setValid(ret.getValid());
                    respBody.buildRespStatusCode(RespStatus.Success);
                }else{
                    respBody.buildRespStatusCode(RespStatus.Failure);
                    if(ret != null) {
                        respBody.setMsg(ret.getRetCode() + ":" + ret.getRetMsg());
                    }
                }
            }catch (Throwable ex){
                log.error("sendSms-sendSmsValid(channel: "+ channel +",mobile: "+ mobile +",userSign: "+ userSign +")-exp:" + ex.getMessage(), ex);
                respBody.buildRespStatusCode(RespStatus.Failure);
                respBody.setMsg(ex.getMessage());
            }
            return respBody;
        });
    }

    /**
     * 用户登录 API
     * @param request
     * 请求报文
     * @return 响应报文
     */
    @PostMapping("/login")
    public Response<RespLoginBody> login(@RequestBody @Validated final LoginRequest request){
        log.debug("login(request: {})...", request);
        return action(request, (reqHead, reqBody) -> {
            //检查参数
            Assert.notNull(reqHead.getChannel(), "'channel'不能为空!");
            Assert.hasText(reqBody.getAccount(), "'account'不能为空!");
            Assert.hasText(reqBody.getPassword(), "'password'不能为空!");
            //初始化响应报文体
            final RespLoginBody respBody = new RespLoginBody();
            try{
                //用户认证处理
               final UserCertificate ret = authenService.authen(reqHead.getChannel(), reqBody.getAccount(),
                        reqBody.getPassword(), reqBody.getMac(),
                        reqBody.getValid(), reqBody.getValidCode());
               if(ret != null){
                   //设置用户登录令牌
                   respBody.setToken(ret.getToken());
                   //设置刷新令牌
                   respBody.setRefreshToken(ret.getRefreshToken());
                   //设置用户信息
                   respBody.setUser(ret.getUser());
                   //
                   respBody.buildRespStatusCode(RespStatus.Success);
               }else{
                   respBody.buildRespStatusCode(RespStatus.Failure);
               }
            }catch (AuthenException ex){
                log.warn("login(reqHead:"+ reqHead +", reqBody:"+ reqBody +")-exp:" + ex.getMessage(), ex);
                respBody.setCode(ex.getCode());
                respBody.setMsg(ex.getMessage());
            }
            return respBody;
        });
    }

    /**
     * 刷新令牌 API
     * @param request
     * 请求报文
     * @return 响应报文
     */
    @PostMapping("/refresh")
    public Response<RespRefreshBody> refreshToken(@RequestBody @Validated final RefreshRequest request){
        log.debug("refreshToken(request: {})...", request);
        return action(request, (reqHead, reqBody) -> {
            //检查参数
            Assert.notNull(reqHead.getChannel(), "'channel'不能为空!");
            Assert.hasText(reqBody.getRefreshToken(), "'refreshToken'不能为空!");
            //初始化响应报文体
            final RespRefreshBody respBody = new RespRefreshBody();
            try{
                //刷新令牌
                final TokenUserData tokenUser = authenService.loadUserByRefreshToken(reqHead.getChannel(), reqBody.getRefreshToken());
                if(tokenUser != null){
                    //设置令牌
                    respBody.setToken(tokenUser.getToken());
                    //设置刷新令牌
                    respBody.setRefreshToken(tokenUser.getRefreshToken());
                    //加载用户信息
                    respBody.setUser(authenService.loadUserById(reqHead.getChannel(), tokenUser.getUserId()));
                }else{
                    respBody.buildRespStatusCode(RespStatus.Failure);
                }
            }catch (Throwable ex){
                log.warn("refreshToken(reqHead: "+ reqHead +", reqBody: "+ reqBody +")-exp:" + ex.getMessage(), ex);
                respBody.buildRespStatusCode(RespStatus.Failure);
                respBody.setMsg(ex.getMessage());
            }
            return respBody;
        });
    }

    /**
     * 退出登录 API
     * @param request
     * 请求报文
     * @param httpSession
     * http session
     * @return 响应报文
     */
    @PostMapping("/logout")
    public Response<RespLogoutBody> logout(@RequestBody final Request<ReqLogoutBody> request, final HttpSession httpSession){
        log.debug("logout(request: {})...", request);
        return action(request, (reqHead, reqBody) -> {
            //获取当前用户
            final TokenUser tokenUser = getTokenUser(httpSession);
            //初始化响应报文体
            final RespLogoutBody respBody = new RespLogoutBody();
            try{
                //退出登录处理
                final boolean ret = authenService.logout(reqHead.getChannel(), tokenUser.getUserId());
                log.info("logout(reqHead: {}, reqBody: {})-ret: {}", reqHead, reqBody, ret);
                respBody.buildRespStatusCode(ret ? RespStatus.Success : RespStatus.Unknown);
            }catch (Throwable ex){
                log.warn("logout(reqHead: "+ reqHead +", reqBody: "+ reqBody +")-exp:" + ex.getMessage(), ex);
                respBody.buildRespStatusCode(RespStatus.Failure);
                respBody.setMsg(ex.getMessage());
            }
            return respBody;
        });
    }
}
