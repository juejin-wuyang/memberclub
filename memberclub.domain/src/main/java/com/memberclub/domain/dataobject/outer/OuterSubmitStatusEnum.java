/**
 * @(#)OuterSubmitStatusEnum.java, 四月 05, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.outer;

import com.memberclub.domain.context.purchase.common.MemberOrderStatusEnum;

/**
 * @author yuhaiqiang
 */
public enum OuterSubmitStatusEnum {

    INIT(0, "初始化"),
    SUBMIT_SUCCESS(MemberOrderStatusEnum.SUBMITED.getCode(), "已提单"),
    SUBMIT_FAIL(MemberOrderStatusEnum.FAIL.getCode(), "提单失败"),
    PERFORMED(MemberOrderStatusEnum.PERFORMED.getCode(), "已履约"),
    FINISH(36, "完成"),
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
