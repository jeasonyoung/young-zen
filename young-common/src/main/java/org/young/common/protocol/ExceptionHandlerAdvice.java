package org.young.common.protocol;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.young.common.protocol.response.Response;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * 异常处理
 *
 * @author yangyong young1982@foxmail.com
 * @date 2018/8/7 11:19
 */
@Slf4j
@ControllerAdvice
@ResponseBody
public class ExceptionHandlerAdvice {

    /**
     * 异常处理。
     * @param e
     * 异常。
     * @return 返回。
     */
    @ExceptionHandler(value = { Throwable.class })
    public Response<Serializable> handlerException(@Nonnull final Throwable e) {
        log.warn("handlerException-exp: " + e);
        return RespUtils.createResponse(RespStatus.ErrWithServer, e.getMessage());
    }
}
