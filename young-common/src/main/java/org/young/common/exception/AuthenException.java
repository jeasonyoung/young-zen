package org.young.common.exception;

/**
 * 认证异常
 * @author jeasonyoung
 */
public class AuthenException extends Exception {
    /**
     * 错误代码
     */
    public static int code = 400;

    /**
     * 构造函数。
     * @param message
     * 异常消息。
     */
    public AuthenException(final String message){
        super(message);
    }

    /**
     * 获取错误代码
     * @return 错误代码
     */
    public int getCode(){
        return code;
    }

    /**
     * 验证码错误。
     */
    public static class AuthenValidCodeException extends AuthenException {

        /**
         * 构造函数。
         */
        public AuthenValidCodeException(){
            super("验证码错误");
            code = 401;
        }
    }

    /**
     * 账号为空
     */
    public static class AuthenAccountBlankException extends AuthenException{

        /**
         * 构造函数。
         */
        public AuthenAccountBlankException(){
            super("账号为空!");
            code = 410;
        }
    }

    /**
     * 账号不存在
     */
    public static class AuthenAccountNotExistException extends AuthenException {

        /**
         * 构造函数。
         */
        public AuthenAccountNotExistException(){
            super("账号不存在!");
            code = 411;
        }
    }

    /**
     * 账号被禁用
     */
    public static class AuthenAccountDisableException extends AuthenException {

        /**
         * 构造函数。
         */
        public AuthenAccountDisableException(){
            super("账号被禁用");
            code = 412;
        }
    }

    /**
     * 密码为空
     */
    public static class AuthenPasswordBlankException extends AuthenException{

        /**
         * 构造函数。
         */
        public AuthenPasswordBlankException(){
            super("密码为空!");
            code = 420;
        }
    }

    /**
     * 密码错误
     */
    public static class AuthenPasswordErrorException extends AuthenException {

        /**
         * 构造函数。
         */
        public AuthenPasswordErrorException(){
            super("密码错误!");
            code = 421;
        }
    }
}
