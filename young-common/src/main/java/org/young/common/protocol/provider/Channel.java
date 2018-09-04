package org.young.common.protocol.provider;

import lombok.Data;
import org.young.common.Status;

import java.io.Serializable;

/**
 * 渠道数据接口
 * @author jeasonyoung
 */
@Data
public class Channel implements Serializable {
    /**
     * 渠道号
     */
    private Integer code;
    /**
     * 渠道名称
     */
    private String name;
    /**
     * 渠道简称
     */
    private String abbr;
    /**
     * 校验类型
     */
    private VerifyType verify;
    /**
     * 渠道状态
     */
    private Status status;
}
