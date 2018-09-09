package org.young.wechat.message.receives;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 地理位置消息
 * @author jeasonyoung
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LocationMessage extends BaseNormalMessage {
    /**
     * 地理位置维度
     */
    @XStreamAlias("Location_X")
    private Float locationX;
    /**
     * 地理位置经度
     */
    @XStreamAlias("Location_Y")
    private Float locationY;
    /**
     * 地图缩放大小
     */
    @XStreamAlias("Scale")
    private Integer scale;
    /**
     * 地理位置信息
     */
    @XStreamAlias("Label")
    private String label;
}
