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

    SUB_ORDER_ID(1, "sub_order_id"),
    AFTERSALE_ORDER_ID(2, "aftersale_order_id"),
    ORDER_TRADE_ID(3, "order_trade_id"),
    PREVIEW_TOKEN(4, "preview_token"),
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
