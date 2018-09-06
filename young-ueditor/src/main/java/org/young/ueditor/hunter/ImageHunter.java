package org.young.ueditor.hunter;

import com.google.common.base.Strings;
import org.young.ueditor.PathFormat;
import org.young.ueditor.UploadHandler;
import org.young.ueditor.define.*;
import org.young.ueditor.upload.StorageManager;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 图片抓取器
 * @author jeasonyoung
 *
 */
public class ImageHunter {
	private final String filename;
	private final String savePath;
	private final List<String> allowTypes;
	private final long maxSize;

	private final List<String> filters;
	private final UploadHandler uploader;

    /**
     * 构造函数
     * @param conf
     * 配置
     * @param uploader
     * 上传处理器
     */
	public ImageHunter(final Map<String, Object> conf,final UploadHandler uploader) {
		this.filename = (String) conf.get("filename");
		this.savePath = (String) conf.get("savePath");
		this.maxSize = (Long) conf.get("maxSize");
		this.allowTypes = Arrays.asList((String[]) conf.get("allowFiles"));
		this.filters = Arrays.asList((String[]) conf.get("filter"));
		this.uploader = uploader;
	}

	public State capture(final String[] list) {
		final MultiState state = new MultiState(true);
		if(list != null && list.length > 0) {
            for (String source : list) {
                if(Strings.isNullOrEmpty(source)){
                    continue;
                }
                state.addState(captureRemoteData(source));
            }
        }
		return state;
	}

	private State captureRemoteData(final String urlStr) {
		HttpURLConnection connection;
		URL url;
		String suffix;
		try {
			url = new URL(urlStr);
			if (!validHost(url.getHost())) {
				return new BaseState(false, AppInfo.PreventHost);
			}
			connection = (HttpURLConnection) url.openConnection();
			connection.setInstanceFollowRedirects(true);
			connection.setUseCaches(true);

			if (!validContentState(connection.getResponseCode())) {
				return new BaseState(false, AppInfo.ConnectionError);
			}
			suffix = MimeType.getSuffix(connection.getContentType());
			if (!validFileType(suffix)) {
				return new BaseState(false, AppInfo.NotAllowFileType);
			}
			if (!validFileSize(connection.getContentLength())) {
				return new BaseState(false, AppInfo.MaxSize);
			}
			String savePath = this.getPath(this.savePath, this.filename, suffix);
			//String physicalPath = this.rootPath + savePath;
			final State state = StorageManager.saveFileByInputStream(connection.getInputStream(), PathFormat.format(savePath), uploader);
			if (state.isSuccess()) {
				state.putInfo("source", urlStr);
			}
			return state;
		} catch (Exception e) {
			return new BaseState(false, AppInfo.RemoteFail);
		}
	}

	private String getPath(final String savePath, final String filename, final String suffix) {
		return PathFormat.parse(savePath + suffix, filename);
	}

	private boolean validHost(final String hostname) {
		return !filters.contains(hostname);
	}

	private boolean validContentState(final int code) {
		return HttpURLConnection.HTTP_OK == code;
	}

	private boolean validFileType(final String type) {
		return this.allowTypes.contains(type);
	}

	private boolean validFileSize(final int size) {
		return size < this.maxSize;
	}
}