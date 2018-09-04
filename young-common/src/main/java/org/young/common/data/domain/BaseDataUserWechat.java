package org.young.common.data.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户关联微信-数据
 *
 * @author yangyong young1982@foxmail.com
 * @date 2018/7/24 23:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseDataUserWechat extends BaseDataEntity {
    /**
     * 所属用户ID
     */
    private String userId;
    /**
     * 微信OpenID
     */
    private String openId;
    /**
     * 微信UnionId
     */
    private String unionId;
    /**
     * 状态(0: 停用, 1: 启用, -1: 删除)
     */
    private Integer status;
}
