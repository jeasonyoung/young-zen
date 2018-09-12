package org.young.auth.model;

import lombok.Data;

import java.io.Serializable;


/**
 * 用户数据。
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/24 15:48
 */
@Data
public class UserInfo implements Serializable {
    /**
     * 用户ID
     */
    private String id;
    /**
     * 用户名称(昵称)
     */
    private String name;
    /**
     * 用户头像URL
     */
    private String avatar;
    /**
     * Vip数据
     */
    private UserVipInfo vip;
}