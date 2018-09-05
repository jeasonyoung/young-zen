package org.young.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.young.common.protocol.RespStatus;
import org.young.common.protocol.RespUtils;
import org.young.common.protocol.response.Response;

import java.io.Serializable;

/**
 * 异常处理
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/8/7 11:19
 */
@Slf4j
public class ExceptionHandlerAdvice {

    /**
     * 异常处理。
     * @param e
     * 异常。
     * @return 返回。
     */
    @ExceptionHandler(value = { Exception.class })
    @ResponseBody
    public Response<Serializable> handlerException(final Exception e) {
        log.warn("handlerException-exp: " + e);
        if(e instanceof AuthenException){
            //解析错误代码
            final RespStatus respStatus = RespStatus.parse(((AuthenException)e).getCode());
            if(respStatus != null){
                //创建响应报文
                return RespUtils.createResponse(respStatus);
            }
        }
        return RespUtils.createResponse(RespStatus.ErrWithServer, e.getMessage());
    }
}
