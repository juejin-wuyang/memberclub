/**
 * @(#)OuterType.java, 四月 05, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.outer;

/**
 * @author yuhaiqiang
 */
public enum OuterType {

    INTERNAL_PURCHASE(0, "内部购买"),

    OUTER_PURCHASE(1, "外部系统购买"),

    REDEEM(2, "兑换码"),

    FREE_TAKE(3, "免费领取"),
    ;

    private int code;

    private String name;

    OuterType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static OuterType findByCode(int code) throws IllegalArgumentException {
        for (OuterType item : OuterType.values()) {
            if (item.code == code) {
                return item;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return this.name;
    }

    public int getCode() {
        return this.code;
    }
}
