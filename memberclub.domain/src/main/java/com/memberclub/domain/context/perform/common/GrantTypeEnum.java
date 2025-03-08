/**
 * @(#)GrantTypeEnum.java, 十二月 28, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.perform.common;

/**
 * @author wuyang
 */
public enum GrantTypeEnum {

    GRANT(0, "发放"),
    ACTIVATE(1, "激活"),
    GRANT_SHIP(2, "发资格"),
    ;

    private int value;

    private String name;

    GrantTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static GrantTypeEnum findByCode(int value) throws IllegalArgumentException {
        for (GrantTypeEnum item : GrantTypeEnum.values()) {
            if (item.value == value) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid GrantTypeEnum value: " + value);
    }


    @Override
    public String toString() {
        return this.name;
    }

    public int getCode() {
        return this.value;
    }
}
