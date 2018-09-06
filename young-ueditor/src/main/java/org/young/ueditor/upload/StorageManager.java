package org.young.ueditor.upload;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.young.ueditor.UploadHandler;
import org.young.ueditor.define.AppInfo;
import org.young.ueditor.define.BaseState;
import org.young.ueditor.define.State;

import java.io.*;
import java.util.UUID;

/**
 * 存储管理器
 * @author jeasonyoung
 */
@Slf4j
public class StorageManager {
    private static final int BUFFER_SIZE = 8192;

    /**
     * 保存二进制文件
     *
     * @param data     数据
     * @param path     路径
     * @param uploader 上传处理器
     * @return 保存结果
     */
    public static State saveBinaryFile(final byte[] data, final String path, final UploadHandler uploader) {
        log.debug("saveBinaryFile(path:" + path + ",uploader:" + uploader + ")...");
        State state;
        //临时文件
        final File tmpFile = getTmpFile();
        try {
            final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile));
            bos.write(data);
            bos.flush();
            bos.close();
            //上传到OSS
            state = saveTmpFile(tmpFile, path, uploader);
        } catch (Throwable e) {
            log.error("saveBinaryFile(path:" + path + ")-exp:" + e.getMessage(), e);
            state = new BaseState(false, AppInfo.IOError);
        } finally {
            if (tmpFile.exists()) {
                //删除临时文件
                final boolean ret = tmpFile.delete();
                log.debug("saveBinaryFile(path:" + path + ")-temp del=>" + ret);
            }
        }
        return state;
    }

    /**
     * 保存文件流
     *
     * @param is       文件流
     * @param path     路径
     * @param maxSize  最大尺寸
     * @param uploader 上传处理器
     * @return 保存结果
     */
    public static State saveFileByInputStream(final InputStream is, final String path, final long maxSize, final UploadHandler uploader) {
        log.debug("saveFileByInputStream(path:" + path + ",maxSize:" + maxSize + ",uploader:" + uploader + ")...");
        State state;
        //临时文件
        final File tmpFile = getTmpFile();
        try {
            byte[] dataBuf = new byte[2048];
            //
            final BufferedInputStream bis = new BufferedInputStream(is, BUFFER_SIZE);
            final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile), BUFFER_SIZE);
            int count;
            while ((count = bis.read(dataBuf)) != -1) {
                bos.write(dataBuf, 0, count);
            }
            bos.flush();
            bos.close();
            if (tmpFile.length() > maxSize) {
                final boolean ret = tmpFile.delete();
                log.debug("saveFileByInputStream(path:" + path + ")-temp del=>" + ret);
                return new BaseState(false, AppInfo.MaxSize);
            }
            //上传到OSS
            state = saveTmpFile(tmpFile, path, uploader);
        } catch (IOException e) {
            log.error("saveFileByInputStream(path:" + path + ")-exp:" + e.getMessage(), e);
            state = new BaseState(false, AppInfo.IOError);
        } finally {
            if (tmpFile.exists()) {
                //删除临时文件
                final boolean ret = tmpFile.delete();
                log.debug("saveFileByInputStream(path:" + path + ")-temp del=>" + ret);
            }
        }
        return state;
    }

    /**
     * 保存文件流
     *
     * @param is       文件流
     * @param path     路径
     * @param uploader 上传处理器
     * @return 保存结果
     */
    public static State saveFileByInputStream(final InputStream is, final String path, final UploadHandler uploader) {
        log.debug("saveFileByInputStream(path:" + path + ",uploader:" + uploader + ")...");
        State state;
        //临时文件
        final File tmpFile = getTmpFile();
        try {
            byte[] dataBuf = new byte[2048];
            //
            final BufferedInputStream bis = new BufferedInputStream(is, BUFFER_SIZE);
            final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile), BUFFER_SIZE);
            int count;
            while ((count = bis.read(dataBuf)) != -1) {
                bos.write(dataBuf, 0, count);
            }
            bos.flush();
            bos.close();
            state = saveTmpFile(tmpFile, path, uploader);
        } catch (IOException e) {
            log.error("saveFileByInputStream(path:" + path + ")-exp:" + e.getMessage(), e);
            state = new BaseState(false, AppInfo.IOError);
        } finally {
            if (tmpFile.exists()) {
                //删除临时文件
                final boolean ret = tmpFile.delete();
                log.debug("saveFileByInputStream(path:" + path + ")-temp del=>" + ret);
            }
        }
        return state;
    }

    private static File getTmpFile() {
        final File tmpDir = FileUtils.getTempDirectory();
        final String tmpFileName = UUID.randomUUID().toString();
        return new File(tmpDir, tmpFileName);
    }

    private static State saveTmpFile(final File tmpFile, final String path, final UploadHandler uploader) {
        log.debug("saveTmpFile(path:" + path + ",uploader:" + uploader + ")...");
        State state;
        if (uploader == null) {
            return new BaseState(false, AppInfo.PermissionDenied);
        }
        try {
            final String url = uploader.syncUpload(path, new FileInputStream(tmpFile));
            state = new BaseState(true);
            state.putInfo("size", tmpFile.length());
            state.putInfo("url", url);
        } catch (Throwable e) {
            log.warn("saveTmpFile(path:" + path + ")-exp:" + e.getMessage(), e);
            state = new BaseState(false, AppInfo.IOError);
        }
        return state;
    }
}