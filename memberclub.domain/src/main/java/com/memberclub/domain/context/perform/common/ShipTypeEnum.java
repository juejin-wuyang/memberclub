package com.memberclub.domain.context.perform.common;

public enum ShipTypeEnum {
    MEMBER_SHIP(RightTypeEnum.MEMBERSHIP.getCode(), "会员身份资格"),
    MEMBER_DISCOUNT_PRICE_SHIP(RightTypeEnum.MEMBER_DISCOUNT_PRICE.getCode(), "会员价资格"),
    ;

    private int value;

    private String name;

    ShipTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static ShipTypeEnum findByCode(int value) throws IllegalArgumentException {
        for (ShipTypeEnum item : ShipTypeEnum.values()) {
            if (item.value == value) {
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
        return this.value;
    }
}

