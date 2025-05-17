/**
 * @(#)AftersaleOrderStatusEnum.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.aftersale;

/**
 * @author wuyang
 */
public enum AftersaleOrderStatusEnum {

    INIT(1, "初始化"),
    REVERSE_PERFORM_ING(10, "逆向履约中"),
    REVERSE_PERFORM_SUCCESS(15, "逆向履约成功"),
    REVERSE_BUY_ING(20, "逆向购买中"),
    REVERSE_REFUND_ING(30, "逆向退款中"),
    REFUND_ORDER_SUCCESS(35, "逆向退款成功"),
    REVERSE_PURCHASE_SUCCESS(40, "逆向购买成功"),
    AFTERSALE_SUCCESS(55, "售后成功"),

    ;

    private int value;

    private String name;

    AftersaleOrderStatusEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static AftersaleOrderStatusEnum findByCode(int value) throws IllegalArgumentException {
        for (AftersaleOrderStatusEnum item : AftersaleOrderStatusEnum.values()) {
            if (item.value == value) {
                return item;
            }
        }

        return null;
    }

    public boolean isPerformReversed() {
        return value >= REVERSE_PERFORM_SUCCESS.getCode();
    }

    public boolean isPayOrderRefund() {
        return value >= REFUND_ORDER_SUCCESS.getCode();
    }

    public boolean isPurchaseReversed() {
        return value >= REVERSE_PURCHASE_SUCCESS.getCode();
    }

    public boolean isEquals(int status) {
        return value == status;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int getCode() {
        return this.value;
    }
}
