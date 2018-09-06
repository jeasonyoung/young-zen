package org.young.ueditor.upload;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.young.ueditor.PathFormat;
import org.young.ueditor.UploadHandler;
import org.young.ueditor.define.AppInfo;
import org.young.ueditor.define.BaseState;
import org.young.ueditor.define.FileType;
import org.young.ueditor.define.State;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 二进制上传器
 *
 * @author jeasonyoung
 */
@Slf4j
public class BinaryUploader {

    /**
     * 保存文件
     *
     * @param request  HttpServletRequest
     * @param conf     配置
     * @param uploader 上传处理器
     * @return 保存结果
     */
    public static State save(final HttpServletRequest request, final Map<String, Object> conf, final UploadHandler uploader) {
        log.debug("save(request:" + request + ",conf:" + conf + ",uploader:" + uploader + ")...");
        //
        boolean isAjaxUpload = request.getHeader("X_Requested_With") != null;
        if (!ServletFileUpload.isMultipartContent(request)) {
            return new BaseState(false, AppInfo.NotMultipartContent);
        }
        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
        if (isAjaxUpload) {
            upload.setHeaderEncoding("UTF-8");
        }
        try {
            final MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            final MultipartFile multipartFile = multipartRequest.getFile(conf.get("fieldName").toString());
            log.info("multipartRequest=> {}, multipartFile=> {}" + multipartRequest, multipartFile);
            if (multipartFile == null) {
                return new BaseState(false, AppInfo.NotMultipartContent);
            }
            String savePath = (String) conf.get("savePath");
            String originFileName = multipartFile.getOriginalFilename();
            final String suffix = FileType.getSuffixByFilename(originFileName);
            //
            if (!Strings.isNullOrEmpty(originFileName) && !Strings.isNullOrEmpty(suffix)) {
                originFileName = originFileName.substring(0, originFileName.length() - suffix.length());
            }
            if (!Strings.isNullOrEmpty(suffix)) {
                savePath = savePath + suffix;
            }
            final long maxSize = (Long) conf.get("maxSize");
            //
            final String confKeyAllowFiles = "allowFiles";
            if (!validType(suffix, (String[]) conf.get(confKeyAllowFiles))) {
                return new BaseState(false, AppInfo.NotAllowFileType);
            }
            savePath = PathFormat.parse(savePath, originFileName);
            InputStream is = multipartFile.getInputStream();
            State storageState = StorageManager.saveFileByInputStream(is, PathFormat.format(savePath), maxSize, uploader);
            is.close();
            if (storageState.isSuccess()) {
                storageState.putInfo("type", suffix);
                storageState.putInfo("original", originFileName + suffix);
                storageState.putInfo("title", originFileName);
            }
            return storageState;
        } catch (IOException e) {
            log.error("save(conf:" + conf + ")-exp:" + e.getMessage(), e);
        }
        return new BaseState(false, AppInfo.IOError);
    }

    private static boolean validType(final String type, final String[] allowTypes) {
        List<String> list = Arrays.asList(allowTypes);
        return list.contains(type);
    }
}