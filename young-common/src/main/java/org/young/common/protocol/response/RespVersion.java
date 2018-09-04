package org.young.common.protocol.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新版本数据。
 * @author jeasonyoung
 */
@Data
public class RespVersion implements Serializable {
    /**
     * 版本号
     */
    private Integer version;
    /**
     * 版本名称
     */
    private String name;
    /**
     * 版本描述
     */
    private String remark;
    /**
     * 校验码
     */
    private String checkCode;
    /**
     * 版本下载URL
     */
    private String url;
}
