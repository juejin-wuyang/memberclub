package com.memberclub.domain.dataobject.payment;

import lombok.Getter;

public enum PayChannelTypeEnum {


    BALANCE("BALANCE"),

    FAST_PAY("FAST_PAY"),

    BEHALF("BEHALF"),

    ALIPAY("ALIPAY"),

    TENPAY("TENPAY"),
    ;

    @Getter
    private String name;

    PayChannelTypeEnum(String name) {
        this.name = name;
    }

    public static PayChannelTypeEnum find(String name) throws IllegalArgumentException {
        for (PayChannelTypeEnum item : PayChannelTypeEnum.values()) {
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
