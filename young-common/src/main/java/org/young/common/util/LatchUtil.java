package org.young.common.util;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 门栓工具类
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/10/9 09:37
 */
@Slf4j
public class LatchUtil {
    private final CountDownLatch downLatch;
    private final AtomicInteger count;
    /**
     * 构造函数
     * @param count
     * 门栓个数
     */
    private LatchUtil(final int count){
        this.downLatch = new CountDownLatch(count);
        this.count = new AtomicInteger(count);
    }

    /**
     * 创建门栓处理实例
     * @param count
     * 门栓个数
     * @return 处理实例
     */
    public static LatchUtil create(int count){
        log.debug("create(count: {})...", count);
        return new LatchUtil(count);
    }

    /**
     * 添加线程执行处理
     * @param executor
     * 线程
     * @param command
     * 业务处理
     * @return 工具对象
     */
    public LatchUtil addExecutor(@Nonnull final Executor executor, @Nonnull final Runnable command){
        log.debug("addExecutor(executor: {}, command: {})...", executor, command);
        final int current = this.count.decrementAndGet();
        if(current <= -1){
            throw new RuntimeException("超出门栓个数!");
        }
        //异步线程处理
        executor.execute(()->{
            final long start = System.currentTimeMillis();
            try{
                command.run();
            }catch (Throwable ex){
                log.warn("addExecutor-command-exp:" + ex.getMessage(), ex);
            }finally {
                downLatch.countDown();
                final long interval = System.currentTimeMillis() - start;
                log.info("addExecutor-线程执行时间: {}ms", interval);
            }
        });
        return this;
    }

    /**
     * 等待执行完成
     */
    public void await(){
        final long start = System.currentTimeMillis();
        try {
            downLatch.await();
        }catch (Throwable ex){
            log.warn("await-exp:" + ex.getMessage(), ex);
        }finally {
            final long interval = System.currentTimeMillis() - start;
            log.info("await-线程等待时间: {}ms", interval);
        }
    }
}
