/**
 * @(#)MQEventEnum.java, 一月 12, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.mq;

import lombok.Getter;

/**
 * @author wuyang
 */
public enum MQTopicEnum {

    TRADE_EVENT(1, MQContants.TRADE_EVENT_TOPIC),

    PRE_FINANCE_EVENT(2, MQContants.PRE_FINANCE_EVENT_TOPIC),

    TRADE_PAYMENT_TIMEOUT_EVENT(3, MQContants.TRADE_PAYMENT_TIMEOUT_EVENT);

    private int code;

    @Getter
    private String name;

    MQTopicEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static MQTopicEnum findByCode(int code) throws IllegalArgumentException {
        for (MQTopicEnum item : MQTopicEnum.values()) {
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
