package org.young.common.util;

/**
 * Session 存储常量
 * @author jeasonyoung
 */
public interface SessionConstants {
//    /**
//     * 渠道存储键名。
//     */
//    String SESSION_CHANNEL_KEY  = "channel";

    /**
     * 令牌用户存储键名。
     */
    String SESSION_TOKEN_USER_KEY = "token_user";

    /**
     * 登录失败计数器键名。
     */
    String SESSION_LOGIN_FAIL_COUNT_KEY = "login_fail_count";

    /**
     * 登录验证码状态键名。
     */
    String SESSION_VERIFY_STATUS_KEY = "verify_status";
}
