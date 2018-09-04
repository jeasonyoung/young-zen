package org.young.common.protocol.request;

import org.young.common.PagingQuery;

import java.io.Serializable;

/**
 * 分页请求报文体
 * @param <T>
 *     查询数据类型。
 * @author jeasonyoung
 */
public interface ReqPagingQueryBody<T extends Serializable> extends PagingQuery<T> {

    /**
     * 设置排序字段。
     * @param sort
     * 排序字段。
     */
    void setSort(final String sort);

    /**
     * 设置排序方向。
     * @param order
     * 排序方向。
     */
    void setOrder(final String order);

    /**
     * 设置每页数据。
     * @param rows
     * 每页数据。
     */
    void setRows(final Integer rows);

    /**
     * 设置页索引。
     * @param index
     * 页索引。
     */
    void setIndex(final Integer index);

    /**
     * 设置查询条件。
     * @param query
     * 查询条件。
     */
    void setQuery(final T query);
}
