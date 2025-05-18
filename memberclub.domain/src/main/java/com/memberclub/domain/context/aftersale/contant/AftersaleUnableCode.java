/**
 * @(#)AftersaleUnableCode.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.aftersale.contant;

import com.memberclub.domain.exception.AfterSaleUnableException;
import org.springframework.util.StringUtils;

/**
 * @author wuyang
 */
public enum AftersaleUnableCode {

    INTERNAL_ERROR(1, "内部错误"),

    DEGRADE_AFTERSALE_ERROR(1001, "降级异常,不能退"),
    AFTERSALE_TIMES_EXCEED_ERROR(1002, "超过当天发起售后最大次数"),
    EXPIRE_ERROR(1003, "已过期,不能退"),
    USE_OUT_ERROR(1004, "已用尽,不能退"),
    USE_ERROR(1005, "已使用,不能退"),
    REFUNDED(1006, "已退款完成"),
    NON_PERFORMED(1007, "还未履约成功,不能退"),
    CONDITION_OCCUR(1008, "券包使用情况变化,请刷新重试"),
    REFUND_NONLAST_ORDER_ERROR(1009, "请优先退最后一笔订单"),
    PREVIEW_TOKEN_TIMEOUT_ERROR(1010, "预览token失效"),

    ;

    private int value;

    private String name;

    AftersaleUnableCode(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static AftersaleUnableCode findByCode(int value) throws IllegalArgumentException {
        for (AftersaleUnableCode item : AftersaleUnableCode.values()) {
            if (item.value == value) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid AftersaleUnableCode value: " + value);
    }


    @Override
    public String toString() {
        return this.name;
    }

    public int getCode() {
        return this.value;
    }

    public AfterSaleUnableException newException(String msg, Throwable cause) {
        msg = StringUtils.isEmpty(msg) ? this.toString() : msg;
        AfterSaleUnableException e = new AfterSaleUnableException(this, msg, cause);
        return e;
    }

    public AfterSaleUnableException newException(String msg) {
        msg = StringUtils.isEmpty(msg) ? this.toString() : msg;
        AfterSaleUnableException e = new AfterSaleUnableException(this, msg, null);
        return e;
    }

    public AfterSaleUnableException newException() {
        AfterSaleUnableException e = new AfterSaleUnableException(this, this.toString(), null);
        return e;
    }
}
