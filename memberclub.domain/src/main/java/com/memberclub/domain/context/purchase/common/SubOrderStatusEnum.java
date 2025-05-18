/**
 * @(#)SubOrderPerformStatusEnum.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.purchase.common;

/**
 * @author wuyang
 */
public enum SubOrderStatusEnum {

    INIT(0, "初始化"),
    FAIL(8, "提单失败"),
    SUBMITED(9, "已提单"),
    CANCELED(19, "已取消"),
    PAYED(29, "已支付"),
    /*
        PERFORMING(30, "履约中"),
    */
    PERFORMED(35, "已履约"),
    PORTION_REFUNDED(44, "部分退"),
    REFUNDED(49, "已退款");

    private int code;

    private String name;

    SubOrderStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static SubOrderStatusEnum findByCode(int code) throws IllegalArgumentException {
        for (SubOrderStatusEnum item : SubOrderStatusEnum.values()) {
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
