/**
 * @(#)OuterSubmitStatusEnum.java, 四月 05, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.outer;

/**
 * @author yuhaiqiang
 */
public enum OuterSubmitStatusEnum {

    INIT(0, "初始化"),
    PRE_SUBMIT(1, "预提单"),
    SUBMITTED(2, "已提单"),
    PERFORMED(3, "已履约"),
    FINISH(4, "完成"),
    ;

    private int code;

    private String name;

    OuterSubmitStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static OuterSubmitStatusEnum findByCode(int code) throws IllegalArgumentException {
        for (OuterSubmitStatusEnum item : OuterSubmitStatusEnum.values()) {
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
