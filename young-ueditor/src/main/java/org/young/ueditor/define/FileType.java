package org.young.ueditor.define;

import com.google.common.base.Strings;
import lombok.Getter;

import java.io.Serializable;

/**
 * 文件类型
 * @author jeasonyoung
 */
@Getter
public enum FileType implements Serializable {
    /**
     * jpg
     */
    JPG("JPG",".jpg");

    private final String key, suffix;
    FileType(final String key, final String suffix){
        this.key = key;
        this.suffix = suffix;
    }

    /**
     * 获取后缀
     * @param key
     * 键名
     * @return 后缀
     */
	public static String getSuffix(final String key){
        if(!Strings.isNullOrEmpty(key)){
            for(FileType t : FileType.values()){
                if(key.equalsIgnoreCase(t.getKey())){
                    return t.getSuffix();
                }
            }
        }
        return null;
	}

	/**
	 * 根据给定的文件名,获取其后缀信息
	 * @param filename
     * 文件名。
	 * @return 后缀信息
	 */
	public static String getSuffixByFilename(final String filename){
	    if(!Strings.isNullOrEmpty(filename)) {
            return filename.substring(filename.lastIndexOf(".")).toLowerCase();
        }
        return null;
	}
}