package org.young.ueditor.upload;

import lombok.extern.slf4j.Slf4j;
import org.young.ueditor.UploadHandler;
import org.young.ueditor.define.State;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 同步上传文件
 * @author jeasonyoung
 */
@Slf4j
public class Uploader {
	private final HttpServletRequest request;
	private final Map<String, Object> conf;
	private final UploadHandler uploader;

    /**
     * 构造函数
     * @param request
     * HttpServletRequest
     * @param conf
     * 配置
     * @param uploader
     * 上传处理器
     */
	public Uploader(final HttpServletRequest request, final Map<String, Object> conf, final UploadHandler uploader) {
		this.request = request;
		this.conf = conf;
		this.uploader = uploader;
	}

	public final State doExec() {
		final String filedName = (String) this.conf.get("fieldName");
		final String confKeyIsBase64 = "isBase64";
		//
		State state;
		if (Boolean.parseBoolean(conf.get(confKeyIsBase64).toString())) {
			state = Base64Uploader.save(request.getParameter(filedName), conf, uploader);
		} else {
			state = BinaryUploader.save(request, conf, uploader);
		}
		log.debug("Uploader-doExec-result:\n"+ state);
		return state;
	}
}
