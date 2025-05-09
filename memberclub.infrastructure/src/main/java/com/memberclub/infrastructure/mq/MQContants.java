/**
 * @(#)MQContants.java, 一月 14, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.mq;

/**
 * author: 掘金五阳
 */
public class MQContants {

    public static final String TRADE_EVENT_TOPIC = "com.memberclub.trade.event";

    public static final String TRADE_PAYMENT_TIMEOUT_EVENT = "com.memberclub.trade.payment.timeout.event";

    public static final String TRADE_EVENT_QUEUE_ON_PAY_SUCCESS = "com.memberclub.trade.event.consumer.paysuccess";

    public static final String TRADE_PAYMENT_TIMEOUT_EVENT_QUEUE = "com.memberclub.trade.payment.timeout.event.consumer";

    public static final String PRE_FINANCE_EVENT_TOPIC = "com.memberclub.prefinance.event";

    public static final String TRADE_EVENT_QUEUE_ON_PRE_FINANCE = "com.memberclub.trade.event.consumer.prefinance";

    public static int WAIT_PAY_EXPIRE_SECONDS = 15 * 60;
}