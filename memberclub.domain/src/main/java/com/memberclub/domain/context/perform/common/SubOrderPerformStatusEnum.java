/**
 * @(#)PerformHisStatusEnum.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.perform.common;

/**
 * @author wuyang
 */
public enum SubOrderPerformStatusEnum {

    INIT(0, "init"),

    PERFORMING(12, "performing"),
    IMMEDIATE_PERFORMING(15, "immediate_performing"),
    IMMEDIATE_PERFORED(17, "immediate_performed"),
    DELAY_PERFORMING(20, "delay_performing"),
    DELAY_PERFORMED(25, "delay_performed"),
    PERFORM_FAIL(30, "perform_fail"),
    PERFORM_SUCCESS(35, "perform_success"),
    REVEREING(40, "reversing"),
    PORTION_REVERSED(44, "portion_reversed"),
    COMPLETED_REVERSED(45, "completed_reversed"),
    ;

    private int value;

    private String name;

    SubOrderPerformStatusEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static SubOrderPerformStatusEnum findByCode(int value) throws IllegalArgumentException {
        for (SubOrderPerformStatusEnum item : SubOrderPerformStatusEnum.values()) {
            if (item.value == value) {
                return item;
            }
        }

        return null;
    }

    public static boolean hasPerformed(int status) {
        return status >= PERFORM_SUCCESS.getCode();
    }


    public static int getReversedStatus(boolean completed) {
        return completed ? COMPLETED_REVERSED.getCode() : PORTION_REVERSED.getCode();
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int getCode() {
        return this.value;
    }
}
