package com.memberclub.domain.dataobject.payment;

public enum PayStatusEnum {

    INIT(0, "初始化"),

    WAIT_PAY(1, "初始化"),

    PAY_SUCCESS(2, "初始化"),

    PAY_REFUND(3, "初始化"),

    ;


    private int value;

    private String name;

    PayStatusEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static PayStatusEnum findByCode(int value) throws IllegalArgumentException {
        for (PayStatusEnum item : PayStatusEnum.values()) {
            if (item.value == value) {
                return item;
            }
        }

        return null;
    }

    public boolean isPaid() {
        return this.value >= PAY_SUCCESS.getCode();
    }

    public boolean isUnPaid() {
        return !isPaid();
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int getCode() {
        return this.value;
    }
}
