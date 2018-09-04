package org.young.common.protocol.provider;

import lombok.Getter;

import java.io.Serializable;

/**
 * 渠道校验类型枚举
 * @author jeasonyoung
 */
@Getter
public enum VerifyType implements Serializable {
    /**
     * 不校验
     */
    Not(0, "不校验"),
    /**
     * 校验。
     */
    Has(1, "校验");

    private final int val;
    private final String title;

    /**
     * 构造函数。
     * @param val
     * 枚举值。
     * @param title
     * 枚举标题。
     */
    VerifyType(final int val, final String title){
        this.val = val;
        this.title = title;
    }

    /**
     * 枚举类型转换。
     * @param val
     * 枚举值。
     * @return 枚举。
     */
    public static VerifyType parse(final Integer val){
        if(val != null){
            for(VerifyType t : VerifyType.values()){
                if(t.getVal() == val){
                    return t;
                }
            }
        }
        return VerifyType.Not;
    }
}
