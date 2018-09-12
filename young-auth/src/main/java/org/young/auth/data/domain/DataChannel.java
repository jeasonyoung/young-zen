package org.young.auth.data.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.young.common.data.domain.BaseDataEntity;

/**
 * 渠道-数据
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/24 16:02
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DataChannel extends BaseDataEntity {
    /**
     * 渠道代码
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
     * 校验类型(0:不校验签名, 1:校验签名)
     */
    private Integer type;
    /**
     * 渠道状态(0:停用,1:启用)
     */
    private Integer status;
}
