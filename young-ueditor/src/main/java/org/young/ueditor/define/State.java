package org.young.ueditor.define;

/**
 * 处理状态接口
 * @author jeasonyoung
 */
public interface State {

    /**
     * 是否成功
     * @return 是否成功
     */
	boolean isSuccess();

    /**
     * 加入数据
     * @param name
     * 数据名称
     * @param val
     * 数据值
     */
	void putInfo(final String name, final String val);

    /**
     * 加入数据
     * @param name
     * 数据名称
     * @param val
     * 数据值
     */
	void putInfo(final String name, final long val);

    /**
     * 转换为JSON串
     * @return JSON串
     */
	String toJSONString();
}