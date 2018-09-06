package org.young.ueditor;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.young.ueditor.define.ActionType;
import org.young.ueditor.define.AppInfo;
import org.young.ueditor.define.BaseState;
import org.young.ueditor.define.State;
import org.young.ueditor.hunter.FileManager;
import org.young.ueditor.hunter.ImageHunter;
import org.young.ueditor.upload.Uploader;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Action 入口
 * @author jeasonyoung
 */
@Slf4j
public class ActionEnter {
    private HttpServletRequest request;
    private String actionType;
    private ConfigManager configManager;
    private UploadHandler uploader;

    /**
     * 构造函数。
     *
     * @param configManager 配置管理器。
     */
    public ActionEnter(final ConfigManager configManager) {
        this.configManager = configManager;
    }

    /**
     * 执行处理。
     *
     * @param request  请求对象。
     * @param uploader 上传处理器。
     * @return 返回处理结果。
     */
    public String exec(final HttpServletRequest request, final UploadHandler uploader) {
        this.request = request;
        this.uploader = uploader;
        this.actionType = request.getParameter("action");
        //
        final String callbackName = this.request.getParameter("callback");
        if (!Strings.isNullOrEmpty(callbackName)) {
            if (!validCallbackName(callbackName)) {
                return new BaseState(false, AppInfo.IllEgal).toJSONString();
            }
            return callbackName + "(" + this.invoke() + ");";
        } else {
            return this.invoke();
        }
    }

    private String invoke() {
        final ActionType action = ActionType.parse(actionType);
        if (action == null) {
            return new BaseState(false, AppInfo.InvalidAction).toJSONString();
        }
        if (configManager == null || !configManager.valid()) {
            return new BaseState(false, AppInfo.ConfigError).toJSONString();
        }
        log.info("invoke(actionType: {} ,action: {})...", actionType, action);
        //
        State state = null;
        Map<String, Object> conf;
        switch (action) {
            case Config: {
                return this.configManager.getAllConfig().toString();
            }
            case UploadImage:
            case UploadScrawl:
            case UploadVideo:
            case UploadFile: {
                conf = configManager.getConfig(action);
                state = new Uploader(request, conf, uploader).doExec();
                break;
            }
            case CatchImage: {
                conf = configManager.getConfig(action);
                String[] list = this.request.getParameterValues((String) conf.get("fieldName"));
                state = new ImageHunter(conf, uploader).capture(list);
                break;
            }
            case ListImage:
            case ListFile: {
                conf = configManager.getConfig(action);
                int start = getStartIndex();
                state = new FileManager(conf, uploader).listFile(start);
                break;
            }
            default:
                break;
        }
        return state == null ? null : state.toJSONString();
    }

    private int getStartIndex() {
        try {
            return Integer.parseInt(this.request.getParameter("start"));
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * callback参数验证
     */
    private boolean validCallbackName(final String name) {
        return name.matches("^[a-zA-Z_]+[\\w0-9_]*$");
    }
}