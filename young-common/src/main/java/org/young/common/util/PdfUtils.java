package org.young.common.util;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PDF-工具类
 *
 * @author yangyong
 * @version 1.0
 **/
@Slf4j
public class PdfUtils {
    private static final Pattern PAGES_REGEX_PATTERN = Pattern.compile("(\\d+)$");

    private static final ExecutorService POOLS = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("pools-pdf-%d").build());

    /**
     * 生成PDF文件流
     *
     * @param url    url
     * @param output PDF输出流
     * @throws Exception 异常
     */
    public static synchronized void createPdf(@Nonnull final String url, @Nonnull final OutputStream output) throws Exception {
        //总页数
        final int pages = createPdfToPages(url, output);
        log.info("createPdf(url: {})=> pages: {}", url, pages);
    }

    /**
     * 生成PDF文件流
     *
     * @param url    url
     * @param output PDF输出流
     * @return 总页数
     * @throws Exception 异常
     */
    public static synchronized int createPdfToPages(@Nonnull final String url, @Nonnull final OutputStream output) throws Exception {
        log.debug("createPdf(url: {})...", url);
        //检查参数
        Assert.hasText(url, "'url'不能为空!");
        //总页数数据
        final AtomicInteger totalPages = new AtomicInteger(0);
        //准备执行外部进程
        final ProcessBuilder processBuilder = new ProcessBuilder(Arrays.asList("wkhtmltopdf", url, "-"));
        //执行PDF生成进程
        final Process pdfProcess = processBuilder.start();
        //异步线程处理
        POOLS.execute(() -> {
            //清理错误流
            clearProcessErrorStream(pdfProcess.getErrorStream(), totalPages);
        });
        //数据流读取
        try (InputStream in = pdfProcess.getInputStream()) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf, 0, buf.length)) != -1) {
                output.write(buf, 0, len);
            }
        } catch (Throwable ex) {
            log.error("createPdf(url: " + url + ")-读取PDF流异常-exp:" + ex.getMessage(), ex);
            throw new Exception(ex.getMessage());
        } finally {
            //销毁外部进程
            pdfProcess.destroy();
        }
        return totalPages.get();
    }

    /**
     * 清除进程错误流数据
     *
     * @param in           输入流
     * @param atomRefPages 总页数
     */
    private static void clearProcessErrorStream(@Nullable final InputStream in, @Nonnull final AtomicInteger atomRefPages) {
        if (in != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String context;
                while ((context = reader.readLine()) != null) {
                    if (!Strings.isNullOrEmpty(context)) {
                        context = context.trim();
                        //检查正则表达式
                        final Matcher matcher = PAGES_REGEX_PATTERN.matcher(context);
                        if (matcher.find()) {
                            String pagesStr = "";
                            try {
                                pagesStr = matcher.group(1);
                                final int pages = Integer.parseInt(pagesStr);
                                if (atomRefPages.get() < pages) {
                                    atomRefPages.set(pages);
                                }
                            } catch (NumberFormatException e) {
                                log.warn("clearProcessErrorStream-parseInt(pagesStr: {})-exp: {}", pagesStr, e.getMessage());
                            }
                        }
                        log.debug("clearProcessErrorStream: {}[pages: {}]", context, atomRefPages.get());
                    }
                }
            } catch (Throwable ex) {
                log.warn("clearProcessErrorStream-exp: {}", ex.getMessage());
            }
        }
    }
}
