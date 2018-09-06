package org.young.ueditor.define;

import com.google.common.base.Strings;
import lombok.Getter;

import java.io.Serializable;

/**
 * 请求action类型
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/8/21 19:11
 */
@Getter
public enum ActionType implements Serializable {
    /**
     * 配置
     */
    Config(0, "config"),
    /**
     * 上传图片
     */
    UploadImage(1, "uploadimage"),
    /**
     * 上传涂画
     */
    UploadScrawl(2, "uploadscrawl"),
    /**
     * 上传视频
     */
    UploadVideo(3, "uploadvideo"),
    /**
     * 上传文件
     */
    UploadFile(4, "uploadfile"),
    /**
     * 截图
     */
    CatchImage(5, "catchimage"),
    /**
     * 列表文件
     */
    ListFile(6, "listfile"),
    /**
     * 列表图片
     */
    ListImage(7, "listimage");

    private final int val;
    private final String key;
    ActionType(final int val, final String key){
        this.val = val;
        this.key = key;
    }

    /**
     * 获取请求Action类型
     * @param key
     * 请求键名
     * @return Action类型
     */
    public static ActionType parse(final String key){
        if(!Strings.isNullOrEmpty(key)){
            for(ActionType type : ActionType.values()){
                if(key.equalsIgnoreCase(type.getKey())){
                    return type;
                }
            }
        }
        return null;
    }
}
