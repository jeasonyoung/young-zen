package org.young.ueditor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.young.common.protocol.Constants;
import org.young.ueditor.define.ActionType;

import java.util.Map;

/**
 * 配置管理器
 * @author hancong03@baidu.com
 */
@Slf4j
public final class ConfigManager {
	private JSONObject jsonConfig = null;
    /**
     * 涂鸦上传filename定义
     */
	private final static String SCRAWL_FILE_NAME = "scrawl";
    /**
     * 远程图片抓取filename定义
     */
	private final static String REMOTE_FILE_NAME = "remote";

    /**
     * 构造函数。
     * @param classConfPath
     * JSON配置文件。
     */
    public ConfigManager(final String classConfPath){
		this.initEnv(classConfPath);
	}

    /**
     * 验证配置文件加载是否正确
     * @return 是否正确
     */
    public boolean valid () {
		return this.jsonConfig != null;
	}
	
	public JSONObject getAllConfig () {
		return this.jsonConfig;
	}

    /**
     * 获取Action配置
     * @param action
     * action类型
     * @return Action配置
     */
     public Map<String, Object> getConfig(final ActionType action){
		final Map<String, Object> conf = Maps.newHashMap();
		String savePath = null;
		switch(action) {
            //上传文件
            case UploadFile: {
                conf.put("isBase64", "false");
                conf.put("maxSize", this.jsonConfig.getLong("fileMaxSize"));
                conf.put("allowFiles", this.getArray("fileAllowFiles"));
                conf.put("fieldName", this.jsonConfig.getString("fileFieldName"));
                savePath = this.jsonConfig.getString("filePathFormat");
                break;
            }
            //上传图片
            case UploadImage: {
                conf.put("isBase64", "false");
                conf.put("maxSize", this.jsonConfig.getLong("imageMaxSize"));
                conf.put("allowFiles", this.getArray("imageAllowFiles"));
                conf.put("fieldName", this.jsonConfig.getString("imageFieldName"));
                savePath = this.jsonConfig.getString("imagePathFormat");
                break;
            }
            //上传视频
            case UploadVideo: {
                conf.put("maxSize", this.jsonConfig.getLong("videoMaxSize"));
                conf.put("allowFiles", this.getArray("videoAllowFiles"));
                conf.put("fieldName", this.jsonConfig.getString("videoFieldName"));
                savePath = this.jsonConfig.getString("videoPathFormat");
                break;
            }
            //上传涂鸦
            case UploadScrawl: {
                conf.put("filename", ConfigManager.SCRAWL_FILE_NAME);
                conf.put("maxSize", this.jsonConfig.getLong("scrawlMaxSize"));
                conf.put("fieldName", this.jsonConfig.getString("scrawlFieldName"));
                conf.put("isBase64", "true");
                savePath = this.jsonConfig.getString("scrawlPathFormat");
                break;
            }
            //截图
            case CatchImage: {
                conf.put("filename", ConfigManager.REMOTE_FILE_NAME);
                conf.put("filter", this.getArray("catcherLocalDomain"));
                conf.put("maxSize", this.jsonConfig.getLong("catcherMaxSize"));
                conf.put("allowFiles", this.getArray("catcherAllowFiles"));
                conf.put("fieldName", this.jsonConfig.getString("catcherFieldName") + "[]");
                savePath = this.jsonConfig.getString("catcherPathFormat");
                break;
            }
            //列表图片
            case ListImage: {
                conf.put("allowFiles", this.getArray("imageManagerAllowFiles"));
                conf.put("dir", this.jsonConfig.getString("imageManagerListPath"));
                conf.put("count", this.jsonConfig.getInteger("imageManagerListSize"));
                break;
            }
            //列表文件
            case ListFile: {
                conf.put("allowFiles", this.getArray("fileManagerAllowFiles"));
                conf.put("dir", this.jsonConfig.getString("fileManagerListPath"));
                conf.put("count", this.jsonConfig.getInteger("fileManagerListSize"));
                break;
            }
            default:
                break;
        }
		conf.put("savePath", savePath);
		return conf;
	}
	
	private void initEnv(final String path) {
		log.debug("initEnv-file:" + path);
		try{
		    final String confJsonContent = readFile(path);
		    if(!Strings.isNullOrEmpty(confJsonContent)) {
                this.jsonConfig = JSON.parseObject(confJsonContent);
            }
		} catch (Exception e){
		    log.error("initEnv(path:"+ path +")-exp:" + e.getMessage(), e);
			this.jsonConfig = null;
		}
	}

	private String[] getArray(final String key){
		JSONArray jsonArray = this.jsonConfig.getJSONArray(key);
		String[] result = new String[jsonArray.size()];
		for ( int i = 0, len = jsonArray.size(); i < len; i++ ) {
			result[i] = jsonArray.getString(i);
		}
		return result;
	}

	private String readFile(final String filePath) {
        log.debug("readFile(filePath:" + filePath + ")...");
        try {
            //加载classpath资源数据
            final ClassPathResource resource = new ClassPathResource(filePath);
            //加载文件内容
            return filter(IOUtils.toString(resource.getInputStream(), Constants.CHARSET));
        } catch (Throwable e) {
            log.error("fromClassPathResourceContent(filePath: " + filePath + ")-exp:" + e.getMessage(), e);
        }
        return null;
    }

    /**
     * 过滤输入字符串, 剔除多行注释以及替换掉反斜杠
     * @param input
     * 输入字符串
     * @return 过滤后字符串
     */
	private String filter(final String input) {
		return input.replaceAll( "/\\*[\\s\\S]*?\\*/", "");
	}
}