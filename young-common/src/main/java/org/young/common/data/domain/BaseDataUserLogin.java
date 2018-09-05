package org.young.common.data.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户登录-数据
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/24 23:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseDataUserLogin extends BaseDataEntity {
    /**
     * 所属家长用户ID
     */
    private String userId;
    /**
     * 用户登录IP地址
     */
    private String ipAddr;
    /**
     * 登录设备标识
     */
    private String mac;
    /**
     * 用户登录令牌
     */
    private String token;
    /**
     * 用户登录刷新令牌
     */
    private String refreshToken;
    /**
     * 状态(0:无效, 1:有效)
     */
    private Integer status;
}
