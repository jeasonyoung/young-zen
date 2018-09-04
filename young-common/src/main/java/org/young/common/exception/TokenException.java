package org.young.common.exception;

/**
 * 令牌异常
 * @author jeasonyoung
 */
public class TokenException extends AuthenException {

    /**
     * 构造函数。
     * @param message
     * 异常消息。
     */
    public TokenException(final String message){
        super(message);
        code = 140;
    }

    /**
     * 构造函数。
     */
    public TokenException(){
        this("令牌无效!");
    }

    /**
     * 令牌过期。
     */
    public static class TokenExpireException extends TokenException{

        /**
         * 构造函数。
         */
        public TokenExpireException(){
            super("令牌过期");
            code = 141;
        }
    }

    /**
     * 刷新令牌无效
     */
    public static class TokenRefreshInvalidException extends TokenException {

        /**
         * 构造函数。
         * @param msg
         * 消息。
         */
        public TokenRefreshInvalidException(final String msg){
            super(msg);
            code = 142;
        }

        /**
         * 构造函数。
         */
        public TokenRefreshInvalidException(){
            this("刷新令牌无效");
        }

    }
}
