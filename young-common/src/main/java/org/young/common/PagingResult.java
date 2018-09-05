package org.young.common;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询结果
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/19 15:48
 */
public interface PagingResult<T extends Serializable> extends Serializable {

    /**
     * 获取数据总数。
     * @return 数据总数。
     */
    Long getTotals();

    /**
     * 设置数据总数。
     * @param totals
     * 数据总数。
     */
    void setTotals(final Long totals);

    /**
     * 获取数据集合。
     * @return 数据集合。
     */
    List<T> getRows();

    /**
     * 设置数据集合。
     * @param rows
     * 数据集合。
     */
    void setRows(final List<T> rows);
}
