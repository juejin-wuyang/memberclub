/**
 * @(#)ExtensionType.java, 一月 12, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.common.extension;

/**
 * @author wuyang
 */
public enum ExtensionType {

    PURCHASE(1, "购买"),
    PERFORM_MAIN(2, "履约主流程"),
    PERIOD_PERFORM(3, "周期履约"),
    AFTERSALE(4, "售后"),
    REVERSE_PERFORM(5, "逆向履约"),
    PRE_FINANCE(6, "预结算"),
    COMMON(7, "通用"),
    OUTER_SUBMIT(8, "外部下单"),
    ;

    private int code;

    private String name;

    ExtensionType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ExtensionType findByCode(int code) throws IllegalArgumentException {
        for (ExtensionType item : ExtensionType.values()) {
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
