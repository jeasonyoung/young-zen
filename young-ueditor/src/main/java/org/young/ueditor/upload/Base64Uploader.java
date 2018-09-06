package org.young.ueditor.upload;

import org.apache.commons.codec.binary.Base64;
import org.young.ueditor.PathFormat;
import org.young.ueditor.UploadHandler;
import org.young.ueditor.define.AppInfo;
import org.young.ueditor.define.BaseState;
import org.young.ueditor.define.FileType;
import org.young.ueditor.define.State;

import java.util.Map;

/**
 * Base64上传处理
 *
 * @author jeasonyoung
 */
public final class Base64Uploader {

    /**
     * 保存内容
     *
     * @param content  内容
     * @param conf     配置
     * @param uploader 上传处理器
     * @return 保存结果
     */
    public static State save(final String content, final Map<String, Object> conf, final UploadHandler uploader) {
        byte[] data = decode(content);
        long maxSize = Long.parseLong(conf.get("maxSize").toString());
        if (!validSize(data, maxSize)) {
            return new BaseState(false, AppInfo.MaxSize);
        }
        String suffix = FileType.getSuffix("JPG");
        String originFileName = (String) conf.get("filename");
        String savePath = PathFormat.parse((String) conf.get("savePath"), originFileName);
        savePath = savePath + suffix;
        State storageState = StorageManager.saveBinaryFile(data, PathFormat.format(savePath), uploader);
        if (storageState.isSuccess()) {
            storageState.putInfo("type", suffix);
            storageState.putInfo("original", originFileName + suffix);
            storageState.putInfo("title", originFileName);
        }
        return storageState;
    }

    private static byte[] decode(final String content) {
        return Base64.decodeBase64(content);
    }

    private static boolean validSize(final byte[] data, final long length) {
        return data.length <= length;
    }
}