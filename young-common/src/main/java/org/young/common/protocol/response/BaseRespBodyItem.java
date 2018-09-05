package org.young.common.protocol.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 响应报文体数据项基类
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/24 10:09
 */
@Data
public abstract class BaseRespBodyItem implements Serializable {
    /**
     * 创建时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 更新时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date lastTime;
}
