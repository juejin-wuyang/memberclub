package com.memberclub.domain.context.perform.common;

public enum RightUsedType {
    ASSET(1, "资产类"),
    SHIP(2, "资格类"),
    ;

    private int value;

    private String name;

    RightUsedType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static RightUsedType findByCode(int value) throws IllegalArgumentException {
        for (RightUsedType item : RightUsedType.values()) {
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
