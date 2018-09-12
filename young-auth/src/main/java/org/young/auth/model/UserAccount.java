package org.young.auth.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户账号数据
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/28 13:36
 */
@Data
public class UserAccount implements Serializable {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 用户账号
     */
    private String account;
}
