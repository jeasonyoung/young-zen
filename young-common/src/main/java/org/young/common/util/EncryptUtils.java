package org.young.common.util;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.Assert;
import org.young.common.protocol.Constants;

import javax.annotation.Nonnull;

/**
 * 密码工具类
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/26 16:09
 */
@Slf4j
public class EncryptUtils {
    /**
     * 默认密码
     */
    public static final String DEFAULT_PWD = "888888";

    /**
     * 创建密码密文。
     * @param userId
     * 用户ID。
     * @param source
     * 密码明文。
     * @return 密码密文。
     */
    public static String createEncryptPasswd(@Nonnull final String userId, @Nonnull final String source){
        log.debug("createEncryptPasswd(userId: {}, source: {})...", userId, source);
        //检查参数
        Assert.hasText(userId, "'userId'不能为空!");
        Assert.hasText(source, "'source'不能为空!");
        //
        return createSignatureEncrypt(createSignatureEncrypt(userId) + source);
    }

    /**
     * 创建默认密码密文。
     * @param userId
     * 用户ID。
     * @return 默认密码密文。
     */
    public static String createEncryptDefaultPasswd(@Nonnull final String userId) {
        log.debug("createEncryptDefaultPasswd(userId: {})...", userId);
        //检查参数
        Assert.hasText(userId, "'userId'不能为空!");
        //
        return createEncryptPasswd(userId, DEFAULT_PWD);
    }

    /**
     * 创建数据签名加密。
     * @param source
     * 签名加密前原文。
     * @return 签名加密后密文。
     */
    public static String createSignatureEncrypt(@Nonnull final String source){
        if(Strings.isNullOrEmpty(source)){
            return "";
        }
        return DigestUtils.md5Hex(source.getBytes(Constants.CHARSET));
    }

    /**
     * 创建Sha1签名。
     * @param source
     * 签名前原文。
     * @return 签名后hex.
     */
    public static String createSha1Hex(@Nonnull final String source){
        //检查参数
        if(Strings.isNullOrEmpty(source)){
            return "";
        }
        return DigestUtils.sha1Hex(source);
    }
}
