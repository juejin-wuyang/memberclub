/**
 * @(#)RabbitmqConsumerFacade.java, 一月 14, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.mq.rabbitmq;

import com.memberclub.common.util.JsonUtils;
import com.memberclub.infrastructure.mq.MQQueueEnum;
import com.memberclub.infrastructure.payment.context.PayExpireCheckMessage;
import com.memberclub.sdk.common.SwitchEnum;
import com.memberclub.sdk.purchase.service.biz.PurchaseBizService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static com.memberclub.infrastructure.mq.MQContants.TRADE_EVENT_QUEUE_ON_PRE_FINANCE;
import static com.memberclub.infrastructure.mq.MQContants.TRADE_PAY_EXPIRE_CHECK_QUEUE;

/**
 * author: 掘金五阳
 */
@ConditionalOnProperty(name = "memberclub.infrastructure.mq", havingValue = "rabbitmq", matchIfMissing = true)
@Configuration
public class RabbitmqConsumerConfiguration extends AbstractRabbitmqConsumerConfiguration {


    @Autowired
    private PurchaseBizService purchaseBizService;

    @RabbitListener(queues = {TRADE_EVENT_QUEUE_ON_PRE_FINANCE})
    @RabbitHandler
    public void consumeTradeEventPreFinanceQueue(String value, Channel channel, Message message) throws IOException {
        consumeAndFailRetry(value, channel, message,
                SwitchEnum.TRADE_EVENT_FOR_PRE_FINANCE_RETRY_TIMES,
                MQQueueEnum.TRADE_EVENT_FOR_PRE_FINANCE);
    }

    @RabbitListener(queues = {TRADE_PAY_EXPIRE_CHECK_QUEUE})
    @RabbitHandler
    public void consumePayTimeoutCheckQueue(String value, Channel channel, Message message) throws IOException {
        try {
            purchaseBizService.payTimeoutCheck(JsonUtils.fromJson(value, PayExpireCheckMessage.class));
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
}