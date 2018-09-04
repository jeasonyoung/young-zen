package org.young.common.protocol;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.young.common.PagingQuery;
import org.young.common.PagingResult;
import org.young.common.exception.TokenException;
import org.young.common.protocol.provider.TokenUser;
import org.young.common.protocol.request.ReqHead;
import org.young.common.protocol.request.ReqPagingQueryBody;
import org.young.common.protocol.request.Request;
import org.young.common.protocol.response.RespPagingResultBody;
import org.young.common.protocol.response.Response;
import org.young.common.util.ConvertUtils;
import org.young.common.util.SessionConstants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.List;

/**
 * 控制器基础类
 * @author jeasonyoung
 */
@Slf4j
public abstract class BaseController implements Serializable {

    /**
     * 请求处理。
     * @param request
     * 请求报文。
     * @param handler
     * 业务处理。
     * @return
     * 响应报文。
     */
    protected static <ReqBody extends Serializable, RespBody extends Serializable> Response<RespBody> action(@Nonnull final Request<ReqBody> request, @Nonnull final Handler<ReqBody, RespBody> handler) {
        try {
            log.debug("action(request:" + request + ")...");
            //业务处理
            final RespBody resp = handler.handler(request.getHead(), request.getBody());
            //响应报文处理
            return RespUtils.createResponse(RespStatus.Success, resp);
        }catch (TokenException ex){
            log.warn("action(request:"+ request +")-exp:" + ex.getMessage(), ex);
            RespStatus s = RespStatus.TokenInvalid;
            if(ex instanceof TokenException.TokenExpireException){
                //令牌过期
                s = RespStatus.TokenExpire;
            }else if(ex instanceof TokenException.TokenRefreshInvalidException){
                //刷新令牌无效
                s = RespStatus.RefreshTokenInvalid;
            }
            return RespUtils.createResponse(s, ex.getMessage());
        }catch (Throwable e){
            log.error("action(request:"+ request +")-exp:" + e.getMessage(), e);
            return RespUtils.createResponse(RespStatus.ErrWithServer, e.getMessage());
        }
    }

    /**
     * 请求处理。
     * @param request
     * 请求报文。
     * @param reqBodyClass
     * 请求报文体类型。
     * @param handler
     * 业务处理。
     * @param <ReqBody>
     *     请求报文体类型。
     * @param <RespBody>
     *     响应报文体类型。
     * @return 响应报文。
     */
    protected static <ReqBody extends Serializable, RespBody extends Serializable> Response<RespBody> action(@Nonnull final String request,@Nonnull final Class<ReqBody> reqBodyClass,@Nonnull final Handler<ReqBody, RespBody> handler){
        try{
            log.debug("action(request:"+ request +",reqBodyClass:"+ reqBodyClass +")...");
            Assert.hasText(request, "'request'请求报文为空!");
            Assert.notNull(handler, "handler is null[业务处理器为空]!");
            //报文解码
            final String decoder = CodecUtils.decode(request);
            log.info("action-decode[request:\n {}]=>\n {}", request, decoder);
            //请求报文解析
            final Request req = JSON.parseObject(decoder, Request.class);
            if(req == null){
                throw new Exception("请求报文解析失败!");
            }
            //json对象转换
            final String reqBodyJson = JSON.toJSONString(req.getBody());
            log.debug("reqBodyJson=>" + reqBodyJson);
            //业务处理
            final RespBody respBody = handler.handler(req.getHead(), JSON.parseObject(reqBodyJson, reqBodyClass));
            //响应报文处理
            return RespUtils.createResponse(RespStatus.Success, respBody);
        }catch (Throwable e){
            log.error("action("+ request +")-exp:" + e.getMessage(), e);
            return RespUtils.createResponse(RespStatus.ErrWithServer, e.getMessage());
        }
    }

    /**
     * 构建分页响应数据。
     * @param resp
     * 响应数据。
     * @param reqQuery
     * 查询请求。
     * @param listener
     * 数据处理器。
     * @param <Resp>
     *     响应数据类型。
     * @param <ReqQry>
     *     查询数据类型。
     * @param <Qry>
     *     查询类型。
     * @param <Ret>
     *     响应类型。
     */
    protected static <Resp extends Serializable,ReqQry extends Serializable, Qry extends Serializable, Ret extends Serializable> void buildRespPagingQuery(@Nonnull final RespPagingResultBody<Resp> resp, @Nullable final ReqPagingQueryBody<ReqQry> reqQuery, @Nonnull final PagingQueryListener<ReqQry, Qry, Ret, Resp> listener) {
        log.debug("buildRespPagingQuery(resp:" + resp + ",reqQuery:" + reqQuery + ",listener:" + listener + ")...");
        //查询条件
        final PagingQueryImpl<Qry> query = new PagingQueryImpl<>();
        if (reqQuery != null) {
            //每页数据
            if (reqQuery.getRows() != null && reqQuery.getRows() > 0) {
                query.setRows(reqQuery.getRows());
            }
            //当前页索引
            if (reqQuery.getIndex() != null && reqQuery.getIndex() > 0) {
                query.setIndex(reqQuery.getIndex());
            }
            //排序处理
            if (!Strings.isNullOrEmpty(reqQuery.getSort())) {
                query.setSort(reqQuery.getSort());
                if (!Strings.isNullOrEmpty(reqQuery.getOrder())) {
                    query.setOrder(reqQuery.getOrder());
                }
            }
        }
        try {
            //查询条件数据类型转换
            query.setQuery(listener.convertQuery(reqQuery == null ? null : reqQuery.getQuery()));
        } catch (Throwable e) {
            log.error("buildRespPagingQuery(reqQuery:"+ reqQuery +")-exp:" + e.getMessage(), e);
        }
        log.debug("buildRespPagingQuery-query:" + query);
        QueryResultImpl<Ret> result = null;
        try {
            //查询数据
            result = new QueryResultImpl<>(listener.query(query));
        }catch (Throwable e){
            log.error("buildRespPagingQuery(query:"+ query +")-exp:" + e.getMessage(), e);
        }finally {
            log.debug("buildRespPagingQuery-query_result:" + result);
        }
        //检查结果数据
        if (result == null){
            return;
        }
        //设置数总据量
        resp.setTotals(result.getTotals());
        //数据类型转换
        final List<Resp> rows = ConvertUtils.convertHandler(result.getRows(), listener);
        if (rows != null && rows.size() > 0) {
            //设置数据集合
            resp.setRows(rows);
        }
    }

    /**
     * 构造响应查询结果(用于返回数据集合)。
     * @param resp
     * 响应报文体。
     * @param listener
     * 数据查询处理。
     * @param <Resp>
     *     响应数据类型。
     * @param <Ret>
     *     查询数据类型。
     */
    protected static <Resp extends Serializable, Ret extends Serializable> void buildRespQueryResult(@Nonnull final RespPagingResultBody<Resp> resp, @Nonnull final QueryListener<Ret, Resp> listener){
        log.debug("buildRespQueryResult(resp:"+ resp +",listener:"+ listener +")...");
        try{
            //数据查询
            final List<Ret> items = listener.query();
            if(items == null || items.size() == 0){
                return;
            }
            //数据转换
            final List<Resp> rows = ConvertUtils.convertHandler(items, listener);
            //检查转换后的数据
            if(rows != null && rows.size() > 0) {
                resp.setTotals((long)rows.size());
                resp.setRows(rows);
            }
        }catch (Throwable e){
            log.error("buildRespQueryResult(resp:"+ resp +",listener:"+ listener +")-exp:" + e.getMessage(), e);
        }
    }

    /**
     * 获取令牌用户数据。
     * @param httpSession
     * session对象。
     * @return 令牌用户数据。
     */
    protected static TokenUser getTokenUser(@Nonnull final HttpSession httpSession){
        final TokenUser user = (TokenUser)httpSession.getAttribute(SessionConstants.SESSION_TOKEN_USER_KEY);
        if(user == null || Strings.isNullOrEmpty(user.getUserId())){
            throw new RuntimeException("获取当前用户失败!");
        }
        return user;
    }

    /**
     * 控制器业务处理器。
     */
    protected interface Handler<ReqBody extends Serializable, RespBody extends Serializable> {
        /**
         * 业务处理。
         * @param reqHead
         * 请求报文头。
         * @param reqBody
         * 请求报文体。
         * @return
         * 响应报文体。
         * @throws Exception
         * 异常处理
         */
        RespBody handler(@Nonnull final ReqHead reqHead, @Nonnull final ReqBody reqBody) throws Exception;
    }

    /**
     * 查询处理监听器。
     * @param <Ret>
     *     查询结果类型。
     * @param <Resp>
     *     响应结果类型。
     */
    protected interface QueryListener<Ret extends Serializable,Resp extends Serializable> extends ConvertUtils.ConvertHandler<Ret, Resp> {
        /**
         * 查询数据。
         * @return
         * 查询结果。
         */
        List<Ret> query();
    }

    /**
     * 分页查询处理监听器。
     * @param <Qry>
     *     查询条件类型。
     * @param <Item>
     *     查询结果类型。
     * @param <Ret>
     *     响应数据类型。
     */
    protected interface PagingQueryListener<ReqQry extends Serializable, Qry extends Serializable, Item extends Serializable,Ret extends Serializable> extends ConvertUtils.ConvertHandler<Item, Ret>{

        /**
         * 查询条件转换。
         * @param info
         * 查询请求体。
         * @return 查询条件。
         */
        Qry convertQuery(@Nullable final ReqQry info);

        /**
         * 查询数据。
         * @param query
         * 查询条件。
         * @return 查询结果。
         */
        PagingResult<Item> query(@Nullable final PagingQuery<Qry> query);
    }

    @Data
    private static class PagingQueryImpl<Qry extends Serializable> implements PagingQuery<Qry> {
        /**
         * 排序字段
         */
        private String sort;
        /**
         * 排序方向
         */
        private String order;
        /**
         * 页索引
         */
        private Integer index;
        /**
         * 每页数据
         */
        private Integer rows;
        /**
         * 查询对象
         */
        private Qry query;
    }

    @Data
    private static class QueryResultImpl<Item extends Serializable> implements PagingResult<Item> {
        private Long totals;
        private List<Item> rows;

        QueryResultImpl(@Nullable final PagingResult<Item> result){
            if(result == null){
                return;
            }
            this.setTotals(result.getTotals());
            this.setRows(result.getRows());
        }
    }

}
