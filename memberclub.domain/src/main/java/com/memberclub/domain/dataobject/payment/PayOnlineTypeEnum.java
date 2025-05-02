package com.memberclub.domain.dataobject.payment;

import lombok.Getter;

public enum PayOnlineTypeEnum {

    ONLINE("ONLINE"),

    OFFLINE("OFFLINE");

    @Getter
    private String name;

    PayOnlineTypeEnum(String name) {
        this.name = name;
    }

    public static PayOnlineTypeEnum find(String name) throws IllegalArgumentException {
        for (PayOnlineTypeEnum item : PayOnlineTypeEnum.values()) {
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
