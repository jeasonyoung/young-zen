package org.young.auth.data.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.young.common.data.domain.BaseDataEntity;

/**
 * 渠道认证关联处理器-数据
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/24 16:12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DataChannelAuthBean extends BaseDataEntity {
    /**
     * 渠道ID
     */
    private String channelId;
    /**
     * 排序号
     */
    private Integer ids;
    /**
     * Bean名称
     */
    private String beanName;
}
