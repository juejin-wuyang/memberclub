/**
 * @(#)TaskTypeEnum.java, 十二月 29, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.oncetask.common;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author wuyang
 */
public enum TaskTypeEnum {

    PERIOD_PERFORM(1, "周期履约"),
    FINANCE_EXPIRE(2, "结算过期任务"),
    AFTERSALE_EXPIRE_REFUND(3, "售后过期退"),
    ;

    private int value;

    private String name;

    TaskTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static TaskTypeEnum findByCode(int value) throws IllegalArgumentException {
        for (TaskTypeEnum item : TaskTypeEnum.values()) {
            if (item.value == value) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid TaskTypeEnum value: " + value);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int getCode() {
        return this.value;
    }
}
