/**
 * @(#)RabbitRegisterConfiguration.java, 一月 20, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.mq;

import com.memberclub.common.log.CommonLog;
import com.memberclub.common.util.ApplicationContextUtils;
import com.memberclub.infrastructure.dynamic_config.DynamicConfig;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * author: 掘金五阳
 */
@Configuration
public class RabbitRegisterConfiguration {


    public static final String DEAD_LETTER_EXCHANGE = "dead_letter_exchange";

    public static FanoutExchange newFanoutExchange(String topic) {
        return new FanoutExchange(topic, true, false);
    }

    public static TopicExchange newTopicExchnage(String topic) {
        return (TopicExchange) ExchangeBuilder.topicExchange(topic).durable(true).build();
    }

    @Bean
    public FanoutExchange tradeEventExchange() {
        return newFanoutExchange(MQContants.TRADE_EVENT_TOPIC);
    }

    @Bean
    public FanoutExchange preFinanceExchange() {
        return new FanoutExchange(MQTopicEnum.PRE_FINANCE_EVENT.getName());
    }

    @Bean
    public FanoutExchange payExpireCheckExchange() {
        return newFanoutExchange(MQContants.TRADE_PAYMENT_TIMEOUT_EVENT);
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return newTopicExchnage(DEAD_LETTER_EXCHANGE);
    }


    @Configuration
    static class TradePaymentTimeoutEventDelayQueueConfiguration extends DelayQueueConfiguration {

        @Override
        public MQQueueEnum getQueue() {
            return MQQueueEnum.TRADE_PAYMENT_TIMEOUT_EVENT_QUEUE;
        }

        @Bean("TRADE_PAYMENT_TIMEOUT_EVENT_QUEUE")
        @Override
        public Queue newNormalQueue() {
            return super.newNormalQueue();
        }

        @Bean("TRADE_PAYMENT_TIMEOUT_EVENT_QUEUE_BINDING_DEAD_EXCHANGE")
        @Override
        public Binding normalQueueBindingDeadExchange() {
            return super.normalQueueBindingDeadExchange();
        }

        @Bean("TRADE_PAYMENT_TIMEOUT_EVENT_DELAY_QUEUE")
        public Queue delayQueue() {
            DynamicConfig config = ApplicationContextUtils.getContext().getBean(DynamicConfig.class);
            int waitTime = config.getInt("wait_pay_time_seconds", MQContants.WAIT_PAY_EXPIRE_SECONDS) * 1000;
            CommonLog.warn("wait_pay_time_seconds is set to " + waitTime);
            return super.delayQueue(waitTime);
        }

        @Bean("TRADE_PAYMENT_TIMEOUT_EVENT_DELAY_QUEUE_BINDING_NORMAL_EXCHANGE")
        @Override
        public Binding delayQueueBindingNormalExchange() {
            return super.delayQueueBindingNormalExchange();
        }
    }

    static abstract class DelayQueueConfiguration {
        public abstract MQQueueEnum getQueue();

        public org.springframework.amqp.core.Queue newNormalQueue() {
            return QueueBuilder.durable(getQueue().getQueneName()).build();
        }

        //不再绑定NormalExchange
       /* public org.springframework.amqp.core.Binding normalQueueBindingNormalExchange() {
            return BindingBuilder.bind(newNormalQueue())
                    .to(newFanoutExchange(getQueue().getTopicName()));
        }*/
        public org.springframework.amqp.core.Binding normalQueueBindingDeadExchange() {
            return BindingBuilder.bind(newNormalQueue())
                    .to(newTopicExchnage(DEAD_LETTER_EXCHANGE))
                    .with(getQueue().getQueneName());
        }

        public org.springframework.amqp.core.Queue delayQueue(int waitTime) {
            return QueueBuilder.durable(getQueue().getDelayQueneName())
                    .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                    .deadLetterRoutingKey(getQueue().getQueneName())
                    .ttl(waitTime)
                    .build();
        }

        public org.springframework.amqp.core.Binding delayQueueBindingNormalExchange() {
            return BindingBuilder.bind(delayQueue(0))
                    .to(newTopicExchnage(getQueue().getTopicName()))
                    .with(getQueue().getDelayQueneName());
        }

    }


    @Configuration
    static class TradeEventPreFinanceQueueConfiguration extends DelayRetryableQueueConfiguration {

        @Override
        public MQQueueEnum getQueue() {
            return MQQueueEnum.TRADE_EVENT_FOR_PRE_FINANCE;
        }

        @Bean("TRADE_EVENT_FOR_PRE_FINANCE")
        @Override
        public Queue newNormalQueue() {
            return super.newNormalQueue();
        }

        @Bean("TRADE_EVENT_FOR_PRE_FINANCE_BINDING_NORMAL_EXCHANGE")
        @Override
        public Binding normalQueueBindingNormalExchange() {
            return super.normalQueueBindingNormalExchange();
        }

        @Bean("TRADE_EVENT_FOR_PRE_FINANCE_BINDING_DEAD_EXCHANGE")
        @Override
        public Binding normalQueueBindingDeadExchange() {
            return super.normalQueueBindingDeadExchange();
        }

        @Bean("TRADE_EVENT_FOR_PRE_FINANCE_DELAY_QUEUE")
        @Override
        public Queue delayQueue() {
            return super.delayQueue();
        }

        @Bean("TRADE_EVENT_FOR_PRE_FINANCE_DELAY_QUEUE_BINDING_DELAY_EXCHANGE")
        @Override
        public Binding delayQueueBindingDelayExchange() {
            return super.delayQueueBindingDelayExchange();
        }
    }

    static abstract class DelayRetryableQueueConfiguration {

        public abstract MQQueueEnum getQueue();

        public org.springframework.amqp.core.Queue newNormalQueue() {
            return QueueBuilder.durable(getQueue().getQueneName()).build();
        }

        public org.springframework.amqp.core.Binding normalQueueBindingNormalExchange() {
            return BindingBuilder.bind(newNormalQueue())
                    .to(newFanoutExchange(getQueue().getTopicName()));
        }

        public org.springframework.amqp.core.Binding normalQueueBindingDeadExchange() {
            return BindingBuilder.bind(newNormalQueue())
                    .to(newTopicExchnage(DEAD_LETTER_EXCHANGE))
                    .with(getQueue().getQueneName());
        }

        public org.springframework.amqp.core.Queue delayQueue() {
            return QueueBuilder.durable(getQueue().getDelayQueneName())
                    .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                    .deadLetterRoutingKey(getQueue().getQueneName())
                    .ttl((int) getQueue().getDelayMillSeconds())
                    .build();
        }

        public org.springframework.amqp.core.Binding delayQueueBindingDelayExchange() {
            return BindingBuilder.bind(delayQueue())
                    .to(newTopicExchnage(DEAD_LETTER_EXCHANGE))
                    .with(getQueue().getDelayQueneName());
        }

    }
}