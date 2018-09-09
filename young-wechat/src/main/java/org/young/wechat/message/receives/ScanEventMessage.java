package org.young.wechat.message.receives;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户已关注时的事件推送
 * @author jeasonyoung
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScanEventMessage extends BaseEventMessage {
    /**
     * 事件KEY值，qrscene_为前缀，后面为二维码的参数值
     */
    @XStreamAlias("EventKey")
    private String eventKey;
    /**
     * 二维码的ticket，可用来换取二维码图片
     */
    @XStreamAlias("Ticket")
    private String ticket;
}
