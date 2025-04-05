/**
 * @(#)PurchaseSourceEnum.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.purchase.common;

import com.memberclub.domain.contants.StringContants;

/**
 * @author wuyang
 */
public enum SubmitSourceEnum {

    HOMEPAGE(StringContants.HOMEPAGE_VALUE, "主页"),

    OUTER_PURCHASE(2, "外部系统购买"),

    REDEEM(3, "兑换码"),

    FREE_TAKE(4, "免费领取"),

    ;

    private int code;

    private String name;

    SubmitSourceEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static SubmitSourceEnum findByCode(int code) throws IllegalArgumentException {
        for (SubmitSourceEnum item : SubmitSourceEnum.values()) {
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
