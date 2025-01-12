/**
 * @(#)TradeEventEnum.java, 一月 12, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.event.trade;


import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;

/**
 * @author yuhaiqiang
 */
public enum TradeEventEnum {

    SUB_ORDER_PAY_SUCCESS(29, "子单支付成功"),
    SUB_ORDER_PERFORM_SUCCESS(35, "子单履约成功"),
    SUB_ORDER_RERVERSE_PERFORM_SUCCESS(45, "子单逆向履约成功"),
    SUB_ORDER_REFUND_SUCCESS(49, "子单退款成功"),
    ;

    private int code;

    private String name;

    TradeEventEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static TradeEventEnum findByCode(int code) throws IllegalArgumentException {
        for (TradeEventEnum item : TradeEventEnum.values()) {
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
