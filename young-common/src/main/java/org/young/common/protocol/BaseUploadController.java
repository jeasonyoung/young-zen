package org.young.common.protocol;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.young.common.util.EncryptUtils;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;


/**
 * 上传处理基类。
 * @author jeasonyoung
 */
@Slf4j
public abstract class BaseUploadController extends BaseAsyncController {

    /**
     * 处理上传文件请求。
     * @param isAsync
     * 是否异步。
     * @param httpServletRequest
     * Http 请求。
     * @param uploadField
     * 文件字段。
     * @param listener
     * 上传后续处理监听器。
     */
    private static void multipartRequestHandler(@Nonnull final Boolean isAsync, @Nonnull final HttpServletRequest httpServletRequest, @Nonnull final String uploadField, @Nonnull final MultipartRequestListener listener){
        log.debug("multipartRequestHandler(isAsync:"+ isAsync +",httpServletRequest:"+ httpServletRequest +",uploadField:"+ uploadField +",listener:"+ listener +")...");
        //检查参数
        Assert.hasText(uploadField, "'uploadField'不能为空!");
        Assert.notNull(listener, "'listener'不能为空!");
        //上传文件处理
        final MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) httpServletRequest;
        final MultipartFile multipartFile = multipartRequest.getFile(uploadField);
        //
        log.debug("multipartRequest=>" + multipartRequest);
        log.debug("multipartFile=>" + multipartFile);
        //
        Assert.notNull(multipartFile, "'multipartFile':["+ uploadField +"]下没有获得文件数据!");
        //
        final String fileName = multipartFile.getOriginalFilename();
        //检查文件名
        Assert.hasText(fileName, "'fileName'上传文件名不能为空!");
        if(Strings.isNullOrEmpty(fileName)) {
            throw new IllegalArgumentException("'fileName'");
        }
        //同步处理
        if(!isAsync){
            try {
                listener.handler(fileName, multipartFile.getInputStream());
            }catch (Throwable e){
                log.error("multipartRequestHandler(uploadField:"+ uploadField +")-exp:" + e.getMessage(), e);
                throw new RuntimeException(e.getMessage());
            }
            return;
        }
        //异步处理
        try {
            //创建临时文件名
            final String tempHashFile = EncryptUtils.createSignatureEncrypt(fileName);
            //缓存数据到临时文件
            final String ext = FilenameUtils.EXTENSION_SEPARATOR + FilenameUtils.getExtension(fileName);
            final File tmpFile = File.createTempFile(tempHashFile, ext);
            final String path = tmpFile.getCanonicalPath();
            log.info("tmpFile=> {}", path);
            //生成文件
            final FileOutputStream outputStream = new FileOutputStream(tmpFile);
            //文件复制
            IOUtils.copy(multipartFile.getInputStream(), outputStream);
            //关闭操作流
            IOUtils.closeQuietly(outputStream);
            //异步处理
            asyncHandler(()->{
                try{
                    listener.handler(fileName, new FileInputStream(tmpFile));
                }catch (Throwable e){
                    log.error("multipartRequestHandler-async-handler(fileName:"+ fileName +")-exp:" + e.getMessage(), e);
                }finally {
                    //删除临时文件
                    final boolean ret = FileUtils.deleteQuietly(tmpFile);
                    log.info("删除临时文件(fileName:"+ fileName +"["+ path +"])=>" + ret);
                }
            });
        }catch (Throwable e){
            log.error("multipartRequestHandler(fileName:"+ fileName +",isAsync:"+ isAsync +")-exp:" + e.getMessage(), e);
        }
    }

    /**
     * 同步处理上传文件请求。
     * @param httpServletRequest
     * Http 请求。
     * @param uploadField
     * 文件字段。
     * @param listener
     * 上传后续处理监听器。
     */
    protected static void syncMultipartRequestHandler(@Nonnull final HttpServletRequest httpServletRequest,@Nonnull final String uploadField,@Nonnull final MultipartRequestListener listener){
        log.debug("syncMultipartRequestHandler(uploadField:"+ uploadField +")...");
        multipartRequestHandler(false, httpServletRequest, uploadField, listener);
    }

    /**
     * 异步处理上传文件请求。
     * @param httpServletRequest
     * Http 请求。
     * @param uploadField
     * 文件字段。
     * @param listener
     * 上传后续处理监听器。
     */
    protected static void asyncMultipartRequestHandler(@Nonnull final HttpServletRequest httpServletRequest,@Nonnull final String uploadField,@Nonnull final MultipartRequestListener listener){
        log.debug("asyncMultipartRequestHandler(uploadField:"+ uploadField +")...");
        multipartRequestHandler(true, httpServletRequest, uploadField, listener);
    }


    /**
     * 上传附件请求处理监听器。
     */
    protected interface MultipartRequestListener {

        /**
         * 处理函数。
         * @param fileName
         * 文件名。
         * @param fileStream
         * 文件流。
         */
        void handler(@Nonnull final String fileName, @Nonnull final InputStream fileStream);
    }
}
