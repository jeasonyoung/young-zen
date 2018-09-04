package org.young.common.data.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *  用户-数据基类
 *
 * @author yangyong young1982@foxmail.com
 * @date 2018/7/23 11:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseDataUser extends BaseDataEntity {
    /**
     * 用户名称(昵称)
     */
    private String name;
    /**
     * 用户头像URL
     */
    private String avatar;
    /**
     * 用户账号
     */
    private String account;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 邮箱地址
     */
    private String email;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 状态(0:停用,1:启用)
     */
    private Integer status;
}