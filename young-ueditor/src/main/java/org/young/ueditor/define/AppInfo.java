package org.young.ueditor.define;

import lombok.Getter;

/**
 * App消息类型
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/8/21 19:32
 */
@Getter
public enum AppInfo {
    /**
     * 成功
     */
    Success(0, "SUCCESS"),
    /**
     * 文件大小超出限制
     */
    MaxSize(1, "文件大小超出限制"),
    /**
     * 权限不足
     */
    PermissionDenied(2, "权限不足"),
    /**
     * 创建文件失败
     */
    FailedCreateFile(3, "创建文件失败"),
    /**
     * IO错误
     */
    IOError(4, "IO错误"),
    /**
     * 上传表单不是multipart/form-data类型
     */
    NotMultipartContent(5, "上传表单不是multipart/form-data类型"),
    /**
     * 解析上传表单错误
     */
    ParseRequestError(6, "解析上传表单错误"),
    /**
     * 未找到上传数据
     */
    NotFoundUploadData(7, "未找到上传数据"),
    /**
     * 不允许的文件类型
     */
    NotAllowFileType(8, "不允许的文件类型"),
    /**
     * 无效的Action
     */
    InvalidAction(101,"无效的Action"),
    /**
     * 配置文件初始化失败
     */
    ConfigError(102, "配置文件初始化失败"),
    /**
     * 被阻止的远程主机
     */
    PreventHost(201,"被阻止的远程主机"),
    /**
     * 远程连接出错
     */
    ConnectionError(202, "远程连接出错"),
    /**
     * 抓取远程图片失败
     */
    RemoteFail(203, "抓取远程图片失败"),
     /**
     * 指定路径不是目录
     */
    NotDirectory(301, "指定路径不是目录"),
    /**
     * 指定路径并不存在
     */
    NotExist(302, "指定路径并不存在"),
    /**
     * callback参数名不合法
     */
    IllEgal(401, "callback参数名不合法");

    private final int val;
    private final String desc;
    AppInfo(final int val, final String desc){
        this.val = val;
        this.desc = desc;
    }

    /**
     * 类型转换
     * @param val
     * 类型值
     * @return 类型
     */
    public static AppInfo parse(final Integer val){
        if(val != null){
            for(AppInfo info : AppInfo.values()){
                if(info.getVal() == val){
                    return info;
                }
            }
        }
        return null;
    }

    /**
     * 消息类型转换
     * @param val
     * 类型值
     * @return 消息
     */
    public static String parseInfo(final Integer val){
        if(val != null) {
            final AppInfo info = parse(val);
            if(info != null){
                return info.getDesc();
            }
        }
        return null;
    }
}
