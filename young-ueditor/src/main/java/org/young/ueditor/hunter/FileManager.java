package org.young.ueditor.hunter;

import lombok.extern.slf4j.Slf4j;
import org.young.ueditor.PathFormat;
import org.young.ueditor.UploadHandler;
import org.young.ueditor.define.BaseState;
import org.young.ueditor.define.MultiState;
import org.young.ueditor.define.State;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 文件管理器
 * @author jeasonyoung
 */
@Slf4j
public class FileManager {
	private final String dir;
	private final int count;
	private final UploadHandler uploader;

    /**
     * 构造函数
     * @param conf
     * 配置集合
     * @param uploader
     * 上传处理接口
     */
	public FileManager(final Map<String, Object> conf, final UploadHandler uploader){
	    this.dir = (String)conf.get("dir");
		this.count = (Integer)conf.get("count");
		this.uploader = uploader;
	}

    /**
     * 列表文件
     * @param index
     * 索引
     * @return 列表状态
     */
	public State listFile(final int index){
	    log.debug("listFile(index:"+ index +")...");
	    //前缀目录处理
        final String prefix = dir.replaceFirst("/","");
		//加载数据
		final List<String> objectList = uploader.listObjectUrls(null, prefix);
        State state;
		if(index < 0 || index > objectList.size()){
            state = new MultiState(true);
        }else{
            state = buildFiles(Arrays.copyOfRange(objectList.toArray(), index,index + count));
        }
        state.putInfo("start", index);
        state.putInfo("total", objectList.size());
		return state;
	}

	private static State buildFiles(final Object[] files){
		final MultiState state = new MultiState(true);
		if(files != null && files.length > 0) {
            for (Object obj : files) {
                if (obj == null) {
                    continue;
                }
                final BaseState fileState = new BaseState(true);
                fileState.putInfo("url", PathFormat.format(obj + ""));
                state.addState(fileState);
            }
        }
		return state;
	}
}