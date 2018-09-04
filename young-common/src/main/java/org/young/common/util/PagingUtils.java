package org.young.common.util;

import lombok.extern.slf4j.Slf4j;
import org.young.common.PagingQuery;
import org.young.common.PagingResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;

/**
 * 分页工具类。
 * @author jeasonyoung
 */
@Slf4j
public class PagingUtils {

    /**
     * 查询数据处理。
     * @param listener
     * 查询监听器。
     * @param <Ret>
     *     输出结果。
     * @param <Qry>
     *     查询条件。
     * @param <Item>
     *     数据转换。
     * @return 返回集合。
     */
    public static <Ret extends Serializable, Qry extends Serializable,Item extends Serializable> List<Ret> queryHandler(@Nullable final Qry query,@Nonnull final QueryListener<Qry, Item, Ret> listener){
        log.debug("queryHandler(query:"+ query +", listener:" + listener +")...");
        try{
            //查询数据
            final List<Item> items = listener.query(query);
            if(items != null && items.size() > 0){
                //输出数据
                return ConvertUtils.convertHandler(items, listener);
            }
        }catch (Throwable e){
            log.error("queryHandler(query:"+ query +",listener:"+ listener +")-exp:" + e.getMessage(), e);
        }
        return null;
    }

    /**
     * 构建分页查询数据。
     * @param pagingResult
     * 分页查询结果。
     * @param pagingQuery
     * 分页查询条件。
     * @param listener
     * 查询数据处理器。
     * @param handler
     * 数据类型转换器。
     * @param <Ret>
     *     查询结果类型。
     * @param <Qry>
     *     查询条件类型。
     * @param <Item>
     *     数据类型。
     */
    public static <Ret extends Serializable, Qry extends Serializable,Item extends Serializable> void buildPagingQuery(@Nonnull final PagingResult<Ret> pagingResult, @Nullable final PagingQuery<Qry> pagingQuery, @Nonnull final QueryListener<Qry, Item, Ret> listener, @Nullable final PagingQueryHandler<Qry, Item> handler){
        log.debug("pagingQuery(query:"+ pagingQuery +",listener:"+ listener +",handler:"+ handler +")...");
        try{
            //查询条件
            final Qry query = pagingQuery == null ? null : pagingQuery.getQuery();
            //启用分页查询
            if(handler != null){
                handler.startPaging(pagingQuery);
            }
            //查询数据
            final List<Item> items = listener.query(query);
            if(items != null){
                //设置数据总数
                if(handler != null) {
                    pagingResult.setTotals(handler.totals(items));
                }
                if(pagingResult.getTotals() == null || pagingResult.getTotals() == 0) {
                    pagingResult.setTotals((long) items.size());
                }
                //设置数据集合
                pagingResult.setRows(ConvertUtils.convertHandler(items, listener));
            }
        } catch (Throwable e){
            log.error("pagingQuery(query:"+ pagingQuery +",listener:"+ listener +")-exp:" + e.getMessage(), e);
        }
    }

    /**
     * 构造数据查询。
     * @param result
     * 查询结果。
     * @param qry
     * 查询条件。
     * @param listener
     * 查询数据处理器。
     * @param <Ret>
     *     查询结果类型。
     * @param <Qry>
     *     查询条件类型。
     * @param <Item>
     *     数据类型。
     */
    public static <Ret extends Serializable, Qry extends Serializable,Item extends Serializable> void buildQuery(@Nonnull final PagingResult<Ret> result, @Nullable final Qry qry,@Nonnull final QueryListener<Qry, Item, Ret> listener) {
        log.debug("query(qry:" + qry + ",listener:" + listener + ")...");
        buildPagingQuery(result, new PagingQueryImpl<>(qry), listener, null);
    }


    /**
     * 查询监听器。
     */
    public interface QueryListener<Qry extends Serializable,Item extends Serializable,Ret extends Serializable> extends ConvertUtils.ConvertHandler<Item, Ret> {
        /**
         * 查询数据。
         * @param query
         * 查询条件。
         * @return
         * 查询结果。
         */
        List<Item> query(@Nullable final Qry query);
    }

    /**
     * 查询数据处理
     */
    public interface PagingQueryHandler<Qry extends Serializable, Item extends Serializable> extends Serializable {

        /**
         * 开始分页。
         * @param query
         * 分页条件。
         */
        void startPaging(final PagingQuery<Qry> query);

        /**
         * 统计数据。
         * @param items
         * 数据集合。
         * @return 统计数据。
         */
        Long totals(final List<Item> items);
    }

    private static class PagingQueryImpl<Qry extends Serializable> implements PagingQuery<Qry> {
        private final Qry query;

        PagingQueryImpl(final Qry query){
            this.query = query;
        }

        @Override
        public String getSort() {
            return null;
        }

        @Override
        public String getOrder() {
            return null;
        }

        @Override
        public Integer getRows() {
            return null;
        }

        @Override
        public Integer getIndex() {
            return null;
        }

        @Override
        public Qry getQuery() {
            return query;
        }
    }
}
