package org.young.common.data.dao;

import java.io.Serializable;

/**
 * 数据处理接口
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/19 15:21
 */
public interface BaseDao<T extends Serializable> {

    /**
     * 1.检查ID是否存在
     * @param id
     * ID
     * @return
     * 是否存在
     */
    boolean has(final String id);

    /**
     * 2.根据ID加载数据
     * @param id
     * ID
     * @return
     * 数据
     */
    T loadById(final String id);

    /**
     * 3.添加数据
     * @param data
     * 数据
     * @return 添加结果
     */
    int add(final T data);

    /**
     * 4.更新数据
     * @param data
     * 数据
     * @return 更新结果
     */
    int update(final T data);

    /**
     * 5.移除数据
     * @param ids
     * 数据ID集合
     * @return 移除结果
     */
    int remove(final String[] ids);
}
