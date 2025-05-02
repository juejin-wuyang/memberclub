package com.memberclub.domain.dataobject.payment;

import lombok.Getter;

public enum PayNodeTypeEnum {


    IMMEDIATE("IMMEDIATE"),//立即支付

    DELIVERY("DELIVERY"),//货到付款

    EXPIRE("EXPIRE"),//到期支付

    ;

    @Getter
    private String name;

    PayNodeTypeEnum(String name) {
        this.name = name;
    }

    public static PayNodeTypeEnum find(String name) throws IllegalArgumentException {
        for (PayNodeTypeEnum item : PayNodeTypeEnum.values()) {
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
