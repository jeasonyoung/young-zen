package org.young.ueditor;

import java.io.InputStream;
import java.util.List;

/**
 * 上传数据处理器
 * @author jeasonyoung
 */
public interface UploadHandler {
    /**
     * 同步上传数据。
     * @param key
     * 上传Key。
     * @param content
     * 上传内容。
     * @return 上传结果访问URL。
     */
    String syncUpload(final String key, final InputStream content);

    /**
     * 列出Object。
     * @param delimiter
     * Delimiter 设置为 “/” 时，返回值就只罗列该文件夹下的文件，可以null。
     * @param prefix
     * Prefix 设为某个文件夹名，就可以罗列以此 Prefix 开头的文件，可以null。
     * @return 集合。
     */
    List<String> listObjectUrls(final String delimiter, final String prefix);
}