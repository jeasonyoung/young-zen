package org.young.auth.services.impl;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.young.auth.config.VerifyCodeProperties;
import org.young.auth.event.SmsValidEvent;
import org.young.auth.model.SmsSendResult;
import org.young.auth.services.SmsValidService;
import org.young.common.Status;
import org.young.common.util.EncryptUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 短信验证-服务接口实现
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/27 18:31
 */
@Slf4j
public abstract class BaseSmsValidServiceImpl implements SmsValidService {

    /**
     * 验证码redis缓存前缀
     */
    private static final String SMS_REDIS_PREFIX = "sms_valid_";

    /**
     * 注入-验证码配置
     */
    @Autowired
    private VerifyCodeProperties config = null;

    /**
     * 注入-spring上下文
     */
    @Autowired
    private ApplicationContext context = null;

    /**
     * 注入-Redis模板
     */
    @Resource(name = "redisTemplate")
    private RedisTemplate<Object, Object> redisTemplate = null;

    /**
     * 构建验证码
     * @param validCodeLength
     * 验证码长度
     * @return 验证码
     */
    protected abstract String buildValidCode(final Integer validCodeLength);

    /**
     * 发送验证码短信消息
     * @param validId
     * 验证码ID
     * @param mobile
     * 手机号码
     * @param validCode
     * 验证码
     * @return 发送结果
     */
    protected abstract SmsSendResult sendValidSms(final String validId,final String mobile, final String validCode);

    /**
     * 发送短信验证码。
     * @param channel
     * 渠道号。
     * @param mobile
     * 手机号码。
     * @return 发送反馈。
     */
    @Override
    public SmsResult sendSmsValid(@Nonnull final Integer channel, @Nonnull final String mobile, @Nullable final String userSign) {
        log.debug("sendSmsValid(channel: {}, mobile: {})...", channel, mobile);
        //检查参数
        Assert.notNull(channel, "'channel'不能为空!");
        Assert.notNull(mobile, "'mobile'不能为空!");
        //生成随机验证码
        final String validId = EncryptUtils.createSignatureEncrypt(UUID.randomUUID().toString() + System.currentTimeMillis());
        final String validCode = buildValidCode(config.getLength());
        //缓存验证码
        saveValidToRedis(validId, validCode);
        //初始化SMS发送结果
        final SmsResult smsResult = new SmsResult();
        //设置验证码ID
        smsResult.setValid(validId);
        try {
            //短信发送验证码
            final SmsSendResult callback = sendValidSms(validId, mobile, validCode);
            //发送成功处理
            if (callback.getStatus() == Status.Enabled) {
                //短信发送成功处理
                final SmsValidEvent.ValidData data = new SmsValidEvent.ValidData();
                //设置渠道号
                data.setChannel(channel);
                //设置手机号
                data.setMobile(mobile);
                //设置用户标识
                data.setUserSign(userSign);
                //发送广播
                context.publishEvent(new SmsValidEvent(this, data));
            }
            //设置反馈状态
            smsResult.setStatus(callback.getStatus());
            //设置反馈代码
            smsResult.setRetCode(callback.getCode());
            //设置反馈消息
            smsResult.setRetMsg(callback.getMsg());
        }catch (Throwable e){
            log.error("sendSmsValid-sendSmsValid(validId: "+ validId +", mobile: "+ mobile +", code:"+ validCode +")-exp:" + e.getMessage(), e);
            smsResult.setStatus(Status.Disabled);
            smsResult.setRetMsg(e.getMessage());
        }
        //返回
        return smsResult;
    }

    /**
     * 设置验证缓存到Redis
     * @param validId
     * 验证码ID。
     * @param validCode
     * 验证码。
     */
    private void saveValidToRedis(@Nonnull final String validId, @Nonnull final String validCode){
        log.debug("saveValidToRedis(validId: {}, validCode: {})...", validId, validCode);
        final String key = SMS_REDIS_PREFIX + validId;
        this.redisTemplate.opsForValue().set(key, validCode, config.getDuration(), TimeUnit.SECONDS);
    }

    /**
     * 获取从Redis缓存取出验证码。
     * @param validId
     * 验证码ID。
     * @return 验证码。
     */
    private String loadRedisValidCode(@Nonnull final String validId){
        log.debug("loadRedisValidCode(validId: {})...", validId);
        final String key = SMS_REDIS_PREFIX + validId;
        return (String) this.redisTemplate.opsForValue().get(key);
    }

    /**
     * 校验短信验证码。
     * @param valid
     * 验证码ID。
     * @param code
     * 校验码。
     * @return 校验结果。
     */
    @Override
    public boolean verifySmsValid(@Nonnull final String valid, @Nonnull final String code) {
        log.debug("verifySmsValid(valid: {}, code: {})...", valid, code);
        if(!Strings.isNullOrEmpty(valid) && !Strings.isNullOrEmpty(code)){
            //加载验证码
            final String oldCode = loadRedisValidCode(valid);
            if(!Strings.isNullOrEmpty(oldCode)){
                log.info("verifySmsValid(valid: {}, code: {}, oldCode: {})...", valid, code, oldCode);
                return code.equalsIgnoreCase(oldCode);
            }
        }
        return false;
    }
}
