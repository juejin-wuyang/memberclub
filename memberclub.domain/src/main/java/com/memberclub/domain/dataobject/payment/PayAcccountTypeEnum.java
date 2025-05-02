package com.memberclub.domain.dataobject.payment;

import lombok.Getter;

public enum PayAcccountTypeEnum {

    COMMON("COMMON"),//公司统一收银台

    BILATERAL("BILATERAL"),//接入银行卡

    THIRD_PARTY("THIRD_PARTY"),//三方支付

    FOURTH_PARTY("FOURTH_PARTY"),//四方聚合支付

    BALANCE("BALANCE"),   //余额支付

    FAST_PAY("FAST_PAY"),//极速支付，月付

    BEHALF("BEHALF"), //代付

    ;

    @Getter
    private String name;

    PayAcccountTypeEnum(String name) {
        this.name = name;
    }

    public static PayAcccountTypeEnum find(String name) throws IllegalArgumentException {
        for (PayAcccountTypeEnum item : PayAcccountTypeEnum.values()) {
            if (item.name.contains(name)) {
                return item;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return this.name;
    }

}
