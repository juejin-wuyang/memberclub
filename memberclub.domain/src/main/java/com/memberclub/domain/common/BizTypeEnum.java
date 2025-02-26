/**
 * @(#)BizTypeEnum.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.common;

import lombok.Getter;

/**
 * @author 掘金五阳
 */
public enum BizTypeEnum {

    DEFAULT(0, "default_biz"),
    DEMO_MEMBER(1, "demo_member"),
    VIDEO_MEMBER(3, "video_member"),
    MUSIC_MEMBER(4, "music_member"),
    DOUYIN_COUPON_PACKAGE(2, "douyin_coupon_package"),//douyin 优惠券包，支持过期退、多份数购买
    ;

    private int code;

    @Getter
    private String name;

    BizTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public boolean isEqual(int bizType) {
        return this.code == bizType;
    }


    public static BizTypeEnum findByCode(int value) throws IllegalArgumentException {
        for (BizTypeEnum item : BizTypeEnum.values()) {
            if (item.code == value) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid BizTypeEnum code: " + value);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int getCode() {
        return this.code;
    }
}
