package org.young.wechat.message.receives;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 上报地理位置事件
 * @author jeasonyoung
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LocationEventMessage extends BaseEventMessage {
    /**
     * 地理位置纬度
     */
    @XStreamAlias("Latitude")
    private Float latitude;
    /**
     * 地理位置经度
     */
    @XStreamAlias("Longitude")
    private Float longitude;
    /**
     * 地理位置精度
     */
    @XStreamAlias("Precision")
    private Float precision;
}
