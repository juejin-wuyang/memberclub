/**
 * @(#)IdTypeEnum.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.id;

/**
 * @author wuyang
 */
public enum IdTypeEnum {

    SubOrder(1, "perform_his"),
    AFTERSALE_ORDER(2, "aftersale_order"),
    PURCHASE_TRADE(3, "purchase_trade"),
    PURCHASE_SUB_TRADE(4, "purchase_sub_trade"),
    ;

    private int value;

    private String name;

    IdTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static IdTypeEnum findByCode(int value) throws IllegalArgumentException {
        for (IdTypeEnum item : IdTypeEnum.values()) {
            if (item.value == value) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid IdTypeEnum value: " + value);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String toKey() {
        return name + "_distributed_id";
    }

    public int getCode() {
        return this.value;
    }
}
