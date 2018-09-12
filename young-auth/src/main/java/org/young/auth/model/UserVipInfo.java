package org.young.auth.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户VIP信息。
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/27 14:19
 */
@Data
public class UserVipInfo implements Serializable {
    /**
     * vip状态: 0-非VIP, 1-vip,2-vip过期
     */
    private Integer vip;
    /**
     * 开始时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    /**
     * 结束时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}
