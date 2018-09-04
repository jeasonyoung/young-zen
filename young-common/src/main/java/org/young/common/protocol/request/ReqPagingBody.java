package org.young.common.protocol.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询请求报文体
 *
 * @author yangyong young1982@foxmail.com
 * @date 2018/7/19 14:50
 */
@Data
public class ReqPagingBody<T extends Serializable> implements ReqPagingQueryBody<T> {
    /**
     * 排序字段
     */
    private String sort;
    /**
     * 排序方向
     */
    private String order;
    /**
     * 每页数据量
     */
    private Integer rows;
    /**
     * 页索引
     */
    private Integer index;
    /**
     * 查询条件
     */
    private T query;
}
