/**
 * @(#)AssetStatusEnum.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.facade;

/**
 * @author wuyang
 */
public enum AssetStatusEnum {

    UNUSE(0, "unuse"),
    USED(1, "used"),
    FREEZE(3, "freezed"),
    EXPIRE(10, "expired"),
    ;

    private int value;

    private String name;

    AssetStatusEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static AssetStatusEnum findByCode(int value) throws IllegalArgumentException {
        for (AssetStatusEnum item : AssetStatusEnum.values()) {
            if (item.value == value) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid AssetStatusEnum value: " + value);
    }


    @Override
    public String toString() {
        return this.name;
    }

    public int getCode() {
        return this.value;
    }
}
