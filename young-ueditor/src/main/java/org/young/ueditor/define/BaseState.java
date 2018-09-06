package org.young.ueditor.define;

import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Iterator;
import java.util.Map;

/**
 * 处理状态基类
 * @author jeasonyoung
 */
@Data
public class BaseState implements State {
    private final Map<String, String> infoMap = Maps.newHashMap();
	private boolean state = false;
	private String info = null;

    /**
     * 构造函数
     */
	public BaseState(){
		this.state = true;
	}

    /**
     * 构造函数
     * @param state
     * 状态
     */
	public BaseState(final boolean state){
	    this.setState(state);
	}

    /**
     * 构造函数
     * @param state
     * 状态
     * @param info
     * 消息
     */
	public BaseState(final boolean state, final String info){
		this.setState(state);
		this.info = info;
	}

    /**
     * 构造函数
     * @param state
     * 状态
     * @param appInfo
     * 消息枚举
     */
	public BaseState(final boolean state, final AppInfo appInfo) {
		this.setState(state);
		this.setInfo(appInfo);
	}

    /**
     * 是否成功
     * @return 是否成功
     */
	@Override
	public boolean isSuccess() {
		return this.state;
	}

    /**
     * 设置消息
     * @param appInfo
     * 消息枚举
     */
	public void setInfo(final AppInfo appInfo){
	    if(appInfo != null){
	        this.info = appInfo.getDesc();
        }
    }

    @Override
    public void putInfo(final String name, final String val) {
        this.infoMap.put(name, val);
    }

    @Override
    public void putInfo(final String name, final long val) {
        this.putInfo(name, val+"");
    }

    /**
     * 转换为JSON串
     * @return JSON串
     */
	@Override
	public String toJSONString() {
		return this.toString();
	}

	@Override
	public String toString () {
	    //初始化数据
        final StringBuilder builder = new StringBuilder("{");
        //结果处理
        final AppInfo appInfo = isSuccess() ? AppInfo.Success : null;
		final String stateVal = appInfo != null ? appInfo.getDesc() : this.info;
		builder.append("\"").append("state").append("\":\"").append(stateVal).append("\"");
		//
		Iterator<String> iterator = this.infoMap.keySet().iterator();
		String key;
		while (iterator.hasNext()) {
			key = iterator.next();
            builder.append(",\"").append(key).append("\":\"").append(infoMap.get(key)).append("\"");
		}
		builder.append("}");
		return builder.toString();
	}

}