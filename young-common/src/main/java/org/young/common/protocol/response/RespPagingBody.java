package org.young.common.protocol.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询响应报文体
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/19 14:53
 */
@Data
public class RespPagingBody<T extends Serializable> implements RespPagingResultBody<T> {
    /**
     * 数据总数
     */
    private Long totals;
    /**
     * 数据集合
     */
    private List<T> rows;
}
