package com.memberclub.sdk.purchase.consumer;

import com.memberclub.common.util.JsonUtils;
import com.memberclub.infrastructure.mq.ConsumeStatauEnum;
import com.memberclub.infrastructure.mq.MQQueueEnum;
import com.memberclub.infrastructure.mq.MessageQueueConsumerFacade;
import com.memberclub.infrastructure.payment.context.PaymentTimeoutMessage;
import com.memberclub.sdk.purchase.service.biz.PurchaseBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayementTimeoutValidateConsumer implements MessageQueueConsumerFacade {

    @Autowired
    private PurchaseBizService purchaseBizService;

    @Override
    public MQQueueEnum register() {
        return MQQueueEnum.TRADE_PAYMENT_TIMEOUT_EVENT_QUEUE;
    }

    @Override
    public ConsumeStatauEnum consume(String message) {
        purchaseBizService.paymentTimeoutValidate(JsonUtils.fromJson(message, PaymentTimeoutMessage.class));
        return ConsumeStatauEnum.success;
    }
}
