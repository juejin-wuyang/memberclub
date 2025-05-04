/**
 * @(#)MQQueueEnum.java, 一月 14, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.mq;

import lombok.Getter;

/**
 * @author wuyang
 */
public enum MQQueueEnum {

    TRADE_EVENT_FOR_PRE_FINANCE(1,
            MQContants.TRADE_EVENT_QUEUE_ON_PRE_FINANCE,
            MQContants.TRADE_EVENT_TOPIC,
            5000),

    TRADE_PAYMENT_TIMEOUT_EVENT_QUEUE(2,
            MQContants.TRADE_PAYMENT_TIMEOUT_EVENT_QUEUE,
            MQContants.TRADE_PAYMENT_TIMEOUT_EVENT, -1),


    TRADE_EVENT_FOR_PAY_SUCCESS(3,
            MQContants.TRADE_EVENT_QUEUE_ON_PAY_SUCCESS,
            MQContants.TRADE_EVENT_TOPIC, 5000),
    ;

    private int code;

    private String queneName;

    private String topicName;

    @Getter
    private long delayMillSeconds;

    MQQueueEnum(int code, String name, String topicName,
                long delayMillSeconds) {
        this.code = code;
        this.queneName = name;
        this.topicName = topicName;
        this.delayMillSeconds = delayMillSeconds;
    }

    public static MQQueueEnum findByCode(int code) throws IllegalArgumentException {
        for (MQQueueEnum item : MQQueueEnum.values()) {
            if (item.code == code) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.queneName;
    }

    public int getCode() {
        return this.code;
    }

    public String getQueneName() {
        return queneName;
    }

    public MQQueueEnum setQueneName(String queneName) {
        this.queneName = queneName;
        return this;
    }

    public String getDelayQueneName() {
        return queneName + "_delay";
    }

    public String getTopicName() {
        return topicName;
    }

    public MQQueueEnum setTopicName(String topicName) {
        this.topicName = topicName;
        return this;
    }
}
