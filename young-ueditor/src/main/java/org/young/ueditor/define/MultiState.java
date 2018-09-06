package org.young.ueditor.define;

import java.util.*;

/**
 * 多状态集合状态
 * 其包含了多个状态的集合, 其本身自己也是一个状态
 * @author jeasonyoung
 */
public class MultiState implements State {
	private boolean state;
	private String info = null;
	private Map<String, Long> intMap = new HashMap<>();
	private Map<String, String> infoMap = new HashMap<>();
	private List<String> stateList = new ArrayList<>();

    /**
     * 构造函数
     * @param state
     * 状态
     */
	public MultiState(final boolean state){
		this.state = state;
	}

    /**
     * 构造函数
     * @param state
     * 状态
     * @param info
     * 消息
     */
	public MultiState(final boolean state, final String info){
		this.state = state;
		this.info = info;
	}

    /**
     * 构造函数。
     * @param state
     * 状态
     * @param infoKey
     * 消息键
     */
	public MultiState(final boolean state, final int infoKey){
		this.state = state;
		this.info = AppInfo.parseInfo(infoKey);
	}

	@Override
	public boolean isSuccess() {
		return this.state;
	}

    /**
     * 添加状态
     * @param state
     * 状态
     */
	public void addState(final State state){
	    if(state != null) {
            stateList.add(state.toJSONString());
        }
	}

	@Override
	public void putInfo(final String name, final String val) {
		this.infoMap.put(name, val);
	}

	@Override
    public void putInfo(final String name, final long val) {
        this.intMap.put(name, val);
    }

    @Override
	public String toJSONString() {
        final StringBuilder builder = new StringBuilder();
        //
        final AppInfo appInfo = isSuccess() ? AppInfo.Success : null;
		String stateVal = appInfo != null ? appInfo.getDesc() : this.info;

		builder.append("{").append("\"state\":").append("\"").append(stateVal).append("\"");
		// 数字转换
		Iterator<String> iterator = this.intMap.keySet().iterator();
		while(iterator.hasNext()){
			stateVal = iterator.next();
			builder.append(",\"").append(stateVal).append("\":").append(intMap.get(stateVal));
		}
		iterator = this.infoMap.keySet().iterator();
		while(iterator.hasNext()){
			stateVal = iterator.next();
			builder.append(",\"").append(stateVal).append("\":\"").append(infoMap.get(stateVal)).append("\"");
		}
		builder.append(", list: [");
		iterator = this.stateList.iterator();
		while(iterator.hasNext()){
			builder.append(iterator.next()).append(",");
		}
		if(stateList.size() > 0){
			builder.deleteCharAt(builder.length() - 1);
		}
		builder.append("]}");
		return builder.toString();
	}
}
