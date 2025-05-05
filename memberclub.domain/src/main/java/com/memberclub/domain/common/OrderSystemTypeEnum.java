/**
 * @(#)OrderSystemTypeEnum.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.common;


/**
 * @author wuyang
 */
public enum OrderSystemTypeEnum {

    ACTUAL_ORDER(1, "actual_order"),//实物订单,可根据实际情况进行扩展
    ;

    private int value;

    private String name;

    OrderSystemTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static OrderSystemTypeEnum findByCode(Integer value) throws IllegalArgumentException {
        if (value == null) {
            return null;
        }
        for (OrderSystemTypeEnum item : OrderSystemTypeEnum.values()) {
            if (item.value == value) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid OrderSystemTypeEnum value: " + value);
    }


    @Override
    public String toString() {
        return this.name;
    }

    public int getCode() {
        return this.value;
    }
}
