package com.memberclub.sdk.perform.consumer;

import com.memberclub.common.util.JsonUtils;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.perform.PerformCmd;
import com.memberclub.domain.context.perform.PerformResp;
import com.memberclub.domain.dataobject.event.trade.TradeEvent;
import com.memberclub.domain.dataobject.event.trade.TradeEventEnum;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.mq.ConsumeStatauEnum;
import com.memberclub.infrastructure.mq.MQQueueEnum;
import com.memberclub.infrastructure.mq.MessageQueueConsumerFacade;
import com.memberclub.sdk.perform.service.PerformBizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PerformOnTradeEventConsumer implements MessageQueueConsumerFacade {


    public static final Logger LOG = LoggerFactory.getLogger(PerformOnTradeEventConsumer.class);

    @Autowired
    private PerformBizService performBizService;

    @Override
    public MQQueueEnum register() {
        return MQQueueEnum.TRADE_EVENT_FOR_PAY_SUCCESS;
    }

    @Override
    public ConsumeStatauEnum consume(String message) {
        TradeEvent event = buildEvent(message);
        if (event == null) {
            LOG.error("event 解析失败:{}", message);
            return ConsumeStatauEnum.success;
        }
        if (event.getEventType() != TradeEventEnum.MAIN_ORDER_PAY_SUCCESS.getCode()) {
            return ConsumeStatauEnum.success;
        }
        LOG.info("收到支付成功  message:{}", message);

        PerformCmd performCmd = new PerformCmd();
        performCmd.setTradeId(event.getDetail().getTradeId());
        performCmd.setBizType(BizTypeEnum.findByCode(event.getDetail().getBizType()));
        performCmd.setUserId(event.getDetail().getUserId());

        PerformResp performResp = performBizService.perform(performCmd);
        if (!performResp.isSuccess() && performResp.isNeedRetry()) {
            throw ResultCode.PERFORM_ERROR.newException();
        }

        return ConsumeStatauEnum.success;
    }

    private TradeEvent buildEvent(String message) {
        try {
            TradeEvent event = JsonUtils.fromJson(message, TradeEvent.class);
            return event;
        } catch (Exception e) {
            LOG.info("解析构建 TradeEvent 异常:{}", message, e);
            throw ResultCode.EXTRACT_MESSAGE_ERROR.newException("解析TradeEvent异常", e);
        }
    }
}
