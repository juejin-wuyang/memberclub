/**
 * @(#)MemberOrderPerformStatusEnum.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.perform.common;

/**
 * @author wuyang
 */
public enum MemberOrderPerformStatusEnum {

    INIT(0, "init"),
    PERFORMING(10, "performing"),
    PERFORM_FAIL(30, "perform_fail"),
    PERFORMED(35, "performed"),
    REVEREING(40, "reversing"),
    PORTION_REVERSED(44, "portion_reversed"),
    COMPLETED_REVERSED(45, "completed_reversed"),
    ;

    private int code;

    private String name;

    MemberOrderPerformStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static MemberOrderPerformStatusEnum findByCode(int code) throws IllegalArgumentException {
        for (MemberOrderPerformStatusEnum item : MemberOrderPerformStatusEnum.values()) {
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
