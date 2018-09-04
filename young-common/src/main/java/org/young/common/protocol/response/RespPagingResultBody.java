package org.young.common.protocol.response;

import org.young.common.PagingResult;

import java.io.Serializable;

/**
 * 分页响应报文体.
 * @param <T>
 *     分页数据类型。
 * @author jeasonyoung
 */
public interface RespPagingResultBody<T extends Serializable> extends PagingResult<T> {

}
