package org.young.common.data.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * 数据实体基类
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/7/23 14:57
 */
@Data
public abstract class BaseDataEntity implements Serializable {
    /**
     * 主键ID
     */
    @Id
    private String id;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date lastTime;

    /**
     * 创建主键ID。
     */
    public void createId(){
        this.setId(UUID.randomUUID().toString());
    }
}