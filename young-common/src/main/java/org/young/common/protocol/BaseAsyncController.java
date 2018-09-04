package org.young.common.protocol;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步处理控制器基类
 * @author jeasonyoung young1982@foxmail.com
 * @date 2018/7/15 15:46
 */
@Slf4j
public abstract class BaseAsyncController extends BaseController {
    private static final ExecutorService POOLS = new ThreadPoolExecutor(
            5,
            200,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024),
            new ThreadFactoryBuilder().setNameFormat("demo-pool-%d").build(),
            new ThreadPoolExecutor.AbortPolicy()
    );


    /**
     * 异步处理。
     * @param handler
     * 异步处理过程。
     */
    protected static void asyncHandler(@Nonnull final Runnable handler){
        log.debug("asyncHandler(handler:"+ handler +")...");
        try {
            POOLS.execute(handler);
        }catch (Throwable e){
            log.error("asyncHandler(handler:"+ handler +")-exp:" + e.getMessage(), e);
        }
    }
}
