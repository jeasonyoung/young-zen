package org.young.ueditor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.young.common.protocol.Constants;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * UEditor Action 服务接口实现抽象类
 *
 * @author jeasonyoung
 */
@Slf4j
public abstract class BaseEditorActionService implements EditorActionService {
    private static final String CHARACTER_ENCODING = Constants.CHARSET.name();
    private static ActionEnter actionEnter = null;

    /**
     * 获取上传处理器
     * @return 上传处理器
     */
    protected abstract UploadHandler getUploader();

    /**
     * 获取配置文件路径
     * @return 配置文件路径
     */
    protected abstract String getConfigPath();

    /**
     * 创建配置管理
     * @return 配置管理
     */
    private ConfigManager createConfigManager() {
        return new ConfigManager(getConfigPath());
    }

    /**
     * 获取Action实例
     * @return Action实例
     */
    private ActionEnter getActionIntance(){
        if(actionEnter == null){
            synchronized (this){
                actionEnter = new ActionEnter(createConfigManager());
            }
        }
        return actionEnter;
    }

    /**
     * 执行处理
     * @param request
     * 请求对象。
     * @param response
     * 响应对象。
     */
    @Override
    public void action(@Nonnull final HttpServletRequest request, @Nonnull final HttpServletResponse response) throws Exception {
        log.debug("action...");
        final UploadHandler uploader = getUploader();
        //检查参数
        Assert.notNull(uploader, "'uploader'未配置!");
        //将请求/响应的编码均设置为UTF-8防止中文乱码
        request.setCharacterEncoding(CHARACTER_ENCODING);
        response.setCharacterEncoding(CHARACTER_ENCODING);
        //
        try (final PrintWriter writer = response.getWriter()){
            //加载Action入口
            final ActionEnter enter = getActionIntance();
            //执行
            final String result = enter.exec(request, uploader);
            log.info("action-result:\n" + result);
            //设置响应json格式
            response.setHeader("Content-Type", "application/json");
            //
            writer.write(result);
            writer.flush();
        }catch (Exception e){
            log.error("action-exp:" + e.getMessage(), e);
        }
    }
}