package org.young.common.data.services.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.young.common.PagingQuery;
import org.young.common.PagingResult;
import org.young.common.util.PagingUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;

/**
 * 数据服务基类
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/19 15:23
 */
@Slf4j
public abstract class BaseDataService {

    /**
     * 启用分页
     * @param query
     * 查询条件
     */
    private void startPage(@Nonnull final PagingQuery<?> query) {
        log.debug("startPage: {}", query);
        //判断分页条件是否满足
        if(query.getIndex() != null && query.getRows() != null){
            //是否排序
            if(Strings.isNullOrEmpty(query.getSort())){
                //分页不排序
                PageHelper.startPage(query.getIndex(), query.getRows());
            }else{
                //分页且排序
                final String orderBy = query.getSort() + (Strings.isNullOrEmpty(query.getOrder()) ? "" : query.getOrder());
                PageHelper.startPage(query.getIndex(), query.getRows(), orderBy);
            }
        }else if(!Strings.isNullOrEmpty(query.getSort())){
            //排序不分页
            final String orderBy = query.getSort() + (Strings.isNullOrEmpty(query.getOrder()) ? "" : query.getOrder());
            PageHelper.orderBy(orderBy);
        }
    }

    /**
     * 获取总数据
     * @param list
     * 分页查询数据集合
     * @param <T>
     *     数据类型
     * @return 总数据
     */
    private <T extends Serializable> long totalOfPage(@Nonnull final List<T> list){
        return PageInfo.of(list).getTotal();
    }

    /**
     * 构建分页查询处理
     * @param pagingResult
     * 分页查询结果
     * @param pagingQuery
     * 分页查询条件
     * @param listener
     * 查询处理监听器
     * @param <Ret>
     *     反馈结果类型
     * @param <Qry>
     *     查询条件类型
     * @param <Item>
     *     查询结果类型
     */
    protected <Ret extends Serializable, Qry extends Serializable,Item extends Serializable> void buildPagingQueryResult(@Nonnull final PagingResult<Ret> pagingResult, @Nullable final PagingQuery<Qry> pagingQuery, @Nonnull final QueryListener<Qry, Item, Ret> listener){
        log.debug("buildPagingQuery(result: {}, query: {}, listener: {})...", pagingResult, pagingQuery, listener);
        //分页查询处理
        PagingUtils.buildPagingQuery(pagingResult, pagingQuery, listener, new PagingUtils.PagingQueryHandler<Qry, Item>() {

            //启动分页查询
            @Override
            public void startPaging(final PagingQuery<Qry> query) {
                //开始分页
                startPage(query);
            }

            //分页结果数据统计
            @Override
            public Long totals(final List<Item> items) {
                //数据处理
                return totalOfPage(items);
            }
        });
    }

    /**
     * 构建分页查询处理
     * @param pagingResult
     * 分页查询结果
     * @param pagingQuery
     * 分页查询条件
     * @param listener
     * 查询处理监听器
     * @param <Ret>
     *     反馈结果类型
     * @param <Qry>
     *     查询条件类型
     * @param <Item>
     *     查询结果类型
     */
    protected <Ret extends Serializable, Qry extends Serializable,Item extends Serializable> void buildPageableQueryResult(@Nonnull final PagingResult<Ret> pagingResult, @Nonnull final PagingQuery<Qry> pagingQuery, @Nonnull final QueryPageableListener<Qry, Item, Ret> listener){
        log.debug("buildPagingQuery(result: {}, query: {}, listener: {})...", pagingResult, pagingQuery, listener);
        //分页查询处理
        PagingUtils.buildPageableQuery(pagingResult, pagingQuery, listener);
    }

    /**
     * 构建查询数据处理
     * @param result
     * 查询结果
     * @param query
     * 查询条件
     * @param listener
     * 查询处理器
     * @param <Ret>
     *     查询结果
     * @param <Qry>
     *     查询条件
     * @param <Item>
     *     数据类型
     */
    protected <Ret extends Serializable, Qry extends Serializable,Item extends Serializable> void buildQueryResult(@Nonnull final PagingResult<Ret> result, @Nullable final Qry query, @Nonnull final QueryListener<Qry, Item, Ret> listener) {
        log.debug("buildQueryResult(result: {}, query: {}, listener: {})...", result, query, listener);
        PagingUtils.buildQuery(result, query, listener);
    }

    /**
     * 查询数据处理
     * @param query
     * 查询条件
     * @param listener
     * 查询处理器
     * @param <Ret>
     *     反馈结果类型
     * @param <Qry>
     *     查询条件类型
     * @param <Item>
     *     查询结果类型
     * @return 查询结果集合
     */
    protected <Ret extends Serializable, Qry extends Serializable,Item extends Serializable> List<Ret> queryHandler(@Nullable final Qry query, @Nonnull final QueryListener<Qry, Item, Ret> listener){
        log.debug("queryHandler(query: {}, listener: {})...", query, listener);
        return PagingUtils.queryHandler(query, listener);
    }

    /**
     * 查询处理监听器
     * @param <Qry>
     *     查询条件类型
     * @param <Item>
     *     查询数据类型
     * @param <Ret>
     *     查询结果类型
     */
    protected interface QueryListener<Qry extends Serializable, Item extends Serializable, Ret extends Serializable> extends PagingUtils.QueryListener<Qry, Item, Ret> {

    }

    /**
     * 查询处理监听器
     * @param <Qry>
     *     查询条件类型
     * @param <Item>
     *     查询数据类型
     * @param <Ret>
     *     查询结果类型
     */
    protected interface QueryPageableListener<Qry extends Serializable, Item extends Serializable, Ret extends Serializable> extends PagingUtils.QueryPageableListener<Qry, Item, Ret> {

    }

    /**
     * 分页查询结果
     * @param <Ret>
     *     查询结果类型
     */
    @Data
    protected class PagingQueryResult<Ret extends Serializable> implements PagingResult<Ret> {
        /**
         * 查询数据总数
         */
        private Long totals;
        /**
         * 查询数据集合
         */
        private List<Ret> rows;
    }
}