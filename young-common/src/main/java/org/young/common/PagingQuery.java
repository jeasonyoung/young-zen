package org.young.common;

import java.io.Serializable;

/**
 * 分页查询条件
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/19 15:48
 */
public interface PagingQuery<T extends Serializable> extends Serializable {

    /**
     * 获取排序字段。
     * @return 排序字段。
     */
    String getSort();

    /**
     * 获取排序方向。
     * @return 排序方向。
     */
    String getOrder();

    /**
     * 获取每页数据。
     * @return 每页数据。
     */
    Integer getRows();

    /**
     * 获取页索引。
     * @return 页索引。
     */
    Integer getIndex();

    /**
     * 获取查询条件。
     * @return 查询条件。
     */
    T getQuery();
}
