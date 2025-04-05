package com.memberclub.domain.dataobject.redeem;

public enum RedeemStatusEnum {
    INIT(0, "初始化"),
    USING(1, "核销中"),
    USED(2, "核销完成"),
    //
    ;

    private int code;

    private String name;

    RedeemStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RedeemStatusEnum findByCode(int code) throws IllegalArgumentException {
        for (RedeemStatusEnum item : RedeemStatusEnum.values()) {
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
