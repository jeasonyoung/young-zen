package org.young.ueditor;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * UEditor Action 服务接口
 * @author jeasonyoung
 */
public interface EditorActionService {

    /**
     * 执行方法。
     * @param request
     * 请求对象。
     * @param response
     * 响应对象。
     * @throws Exception
     * 异常处理
     */
    void action(@Nonnull final HttpServletRequest request, @Nonnull final HttpServletResponse response) throws Exception;
}