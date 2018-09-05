package org.young.common.data.dao;

import org.young.common.data.domain.BaseDataUser;

import javax.annotation.Nonnull;

/**
 * 用户-数据操作基接口
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/23 11:42
 */
public interface BaseUserDao<T extends BaseDataUser> extends BaseDao<T> {

    /**
     * 根据用户账号加载数据。
     * @param account
     * 用户账号。
     * @return 用户数据。
     */
    T loadByAccount(@Nonnull final String account);
}
