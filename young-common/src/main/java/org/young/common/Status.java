package org.young.common;

import lombok.Getter;

import java.io.Serializable;

/**
 * 状态枚举
 * @author jeasonyoung
 */
@Getter
public enum Status implements Serializable {
    /**
     * 删除。
     */
    Delete(-1),
    /**
     * 停用。
     */
    Disabled(0),
    /**
     * 启用。
     */
    Enabled(1);

    private int val;
    Status(final int val){
        this.val = val;
    }

    /**
     * 枚举类型转换。
     * @param val
     * 枚举值。
     * @return 枚举对象。
     */
    public static Status parse(final Integer val) {
        if(val != null) {
            for(Status s : Status.values()){
                if(s.getVal() == val){
                    return s;
                }
            }
        }
        return Status.Disabled;
    }
}
