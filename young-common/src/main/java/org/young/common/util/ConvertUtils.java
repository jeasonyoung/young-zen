package org.young.common.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 数据类型转换工具类。
 * @author jeasonyoung
 */
public class ConvertUtils {

    /**
     * 类型转换处理器。
     * @param <S>
     *     转换源。
     * @param <R>
     *     目标源。
     */
    public interface ConvertHandler<S, R> extends Serializable {
        /**
         * 类型转换处理。
         * @param item
         * 转换前对象。
         * @return 转换后对象。
         */
        R convert(@Nonnull S item);
    }

    /**
     * 数据类型转换。
     * @param items
     * 源数据。
     * @param handler
     * 处理器。
     * @param <S>
     *     源数据类型。
     * @param <R>
     *     目标数据类型。
     * @return 转换结果。
     */
    public static <S, R> List<R> convertHandler(@Nullable final List<S> items, @Nonnull final ConvertHandler<S,R> handler){
        if(items == null || items.size() == 0){
            return null;
        }
        //数据转换
        final List<R> rows = new LinkedList<>();
        for(S item : items){
            if(item == null){
                continue;
            }
            R row = handler.convert(item);
            if (row != null) {
                rows.add(row);
            }
        }
        return rows;
    }
}
