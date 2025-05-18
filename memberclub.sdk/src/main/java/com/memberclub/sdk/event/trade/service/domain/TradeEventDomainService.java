/**
 * @(#)TradeEventDomainService.java, 一月 12, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.event.trade.service.domain;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.collect.Lists;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.util.CollectionUtilEx;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.SubOrderPerformContext;
import com.memberclub.domain.context.perform.common.SubOrderPerformStatusEnum;
import com.memberclub.domain.context.perform.period.PeriodPerformContext;
import com.memberclub.domain.context.perform.reverse.PerformItemReverseInfo;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.domain.context.perform.reverse.SubOrderReversePerformContext;
import com.memberclub.domain.context.purchase.cancel.PurchaseCancelContext;
import com.memberclub.domain.dataobject.event.trade.TradeEventDO;
import com.memberclub.domain.dataobject.event.trade.TradeEventDetailDO;
import com.memberclub.domain.dataobject.event.trade.TradeEventEnum;
import com.memberclub.domain.dataobject.payment.context.PaymentNotifyContext;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.dataobject.task.perform.PerformTaskContentItemDO;
import com.memberclub.infrastructure.mq.MQTopicEnum;
import com.memberclub.infrastructure.mq.MessageQuenePublishFacade;
import com.memberclub.sdk.event.trade.extension.TradeEventDomainExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Service
public class TradeEventDomainService {

    @Autowired
    private MessageQuenePublishFacade messageQuenePublishFacade;

    @Autowired
    private ExtensionManager extensionManager;

    public void publishEventOnPurchaseCancelSuccessForSubOrder(PurchaseCancelContext cancelContext,
                                                               MemberOrderDO memberOrderDO,
                                                               MemberSubOrderDO subOrder) {
        TradeEventDO event = buildTradeEvent(subOrder,
                Lists.newArrayList(),
                TradeEventEnum.SUB_ORDER_CANCEL_SUCCESS,
                1
        );

        String value = extensionManager.getExtension(BizScene.of(subOrder.getBizType()),
                TradeEventDomainExtension.class).onPurchaseCancelSuccessForSubOrder(cancelContext,
                memberOrderDO, subOrder, event);

        messageQuenePublishFacade.publish(MQTopicEnum.TRADE_EVENT, value);
        CommonLog.info("发送 TradeEvent:{}", value);
    }

    public void publishEventOnPerformSuccessForSubOrder(PerformContext performContext,
                                                        SubOrderPerformContext subOrderPerformContext,
                                                        MemberSubOrderDO subOrder) {
        TradeEventDO event = buildTradeEvent(subOrder,
                CollectionUtilEx.mapToList(subOrderPerformContext.getImmediatePerformItems(), MemberPerformItemDO::getItemToken),
                TradeEventEnum.SUB_ORDER_PERFORM_SUCCESS,
                1
        );

        String value = extensionManager.getExtension(BizScene.of(subOrder.getBizType()),
                TradeEventDomainExtension.class).onPerformSuccessForSubOrder(performContext,
                subOrderPerformContext, subOrder, event);

        messageQuenePublishFacade.publish(MQTopicEnum.TRADE_EVENT, value);
        CommonLog.info("发送 TradeEvent:{}", value);
    }

    public void publishEventOnPaySuccess(PaymentNotifyContext context) {
        TradeEventDO event = buildTradeEvent4PaySuccess(context);

        String value = extensionManager.getExtension(BizScene.of(context.getMemberOrderDO().getBizType()),
                TradeEventDomainExtension.class).onPaySuccess(context, event);

        messageQuenePublishFacade.publish(MQTopicEnum.TRADE_EVENT, value);
        CommonLog.info("发送 TradeEvent:{}", value);
    }

    public void publishEventOnPeriodPerformSuccess(PeriodPerformContext context) {
        TradeEventDO event = new TradeEventDO();
        event.setEventType(TradeEventEnum.SUB_ORDER_PERIOD_PERFORM_SUCCESS);
        TradeEventDetailDO detail = new TradeEventDetailDO();
        detail.setEventTime(TimeUtil.now());
        detail.setSkuId(context.getSkuId());
        detail.setTradeId(context.getTradeId());
        detail.setBizType(context.getBizType());
        detail.setSubTradeId(context.getContent().getSubTradeId());
        detail.setUserId(context.getUserId());
        detail.setPerformStatus(SubOrderPerformStatusEnum.PERFORM_SUCCESS);
        detail.setPeriodIndex(context.getContent().getPhase());
        detail.setItemTokens(
                CollectionUtilEx.map(context.getContent().getItems(), PerformTaskContentItemDO::getItemToken)
        );
        event.setDetail(detail);

        String value = extensionManager.getExtension(BizScene.of(context.getBizType()),
                TradeEventDomainExtension.class).onPeriodPerformSuccessForSubOrder(context, event);

        messageQuenePublishFacade.publish(MQTopicEnum.TRADE_EVENT, value);
        CommonLog.info("发送 TradeEvent:{}", value);
    }


    public void publishOnReversePerformSuccessForSubOrder(ReversePerformContext context,
                                                          SubOrderReversePerformContext subOrderReversePerformContext,
                                                          MemberSubOrderDO subOrder) {
        TradeEventDO event = buildTradeEvent(subOrder,
                CollectionUtilEx.mapToList(subOrderReversePerformContext.getItems(), PerformItemReverseInfo::getItemToken),
                TradeEventEnum.SUB_ORDER_RERVERSE_PERFORM_SUCCESS,
                context.getAfterSaleApplyContext().getExecuteCmd().getPeriodIndex());
        String value = extensionManager.getExtension(BizScene.of(subOrder.getBizType()),
                TradeEventDomainExtension.class).onReversePerformSuccessForSubOrder(context,
                subOrderReversePerformContext, subOrder, event);

        messageQuenePublishFacade.publish(MQTopicEnum.TRADE_EVENT, value);
        CommonLog.info("发送 TradeEvent:{}", value);
    }

    public void publishOnRefundSuccessForSubOrder(AfterSaleApplyContext context,
                                                  MemberSubOrderDO subOrder) {
        List<String> itemTokens = CollectionUtilEx.filterAndMap(
                context.getReversablePerformItems(),
                (item) -> StringUtils.equals(String.valueOf(subOrder.getSubTradeId()), item.getSubTradeId()),
                MemberPerformItemDO::getItemToken
        );

        TradeEventDO event = buildTradeEvent(subOrder,
                itemTokens,
                TradeEventEnum.SUB_ORDER_REFUND_SUCCESS,
                context.getExecuteCmd().getPeriodIndex());
        String value = extensionManager.getExtension(BizScene.of(subOrder.getBizType()),
                TradeEventDomainExtension.class).onRefundSuccessForSubOrder(context, subOrder, event);

        messageQuenePublishFacade.publish(MQTopicEnum.TRADE_EVENT, value);
        CommonLog.info("发送 TradeEvent:{}", value);
    }

    public void publishOnFreezeSuccessForSubOrder(AfterSaleApplyContext context,
                                                  MemberSubOrderDO subOrder) {
        List<String> itemTokens = CollectionUtilEx.filterAndMap(
                context.getReversablePerformItems(),
                (item) -> StringUtils.equals(String.valueOf(subOrder.getSubTradeId()), item.getSubTradeId()),
                MemberPerformItemDO::getItemToken
        );

        TradeEventDO event = buildTradeEvent(subOrder,
                itemTokens,
                TradeEventEnum.SUB_ORDER_FREEZE_SUCCESS,
                context.getExecuteCmd().getPeriodIndex());
        String value = extensionManager.getExtension(BizScene.of(subOrder.getBizType()),
                TradeEventDomainExtension.class).onRefundSuccessForSubOrder(context, subOrder, event);

        messageQuenePublishFacade.publish(MQTopicEnum.TRADE_EVENT, value);
        CommonLog.info("发送 TradeEvent:{}", value);
    }


    private TradeEventDO buildTradeEvent4PaySuccess(PaymentNotifyContext context) {
        MemberOrderDO orderDO = context.getMemberOrderDO();
        TradeEventDO event = new TradeEventDO();
        event.setEventType(TradeEventEnum.MAIN_ORDER_PAY_SUCCESS);
        TradeEventDetailDO detail = new TradeEventDetailDO();
        detail.setEventTime(context.getCmd().getPayTime());
        detail.setTradeId(orderDO.getTradeId());
        detail.setBizType(orderDO.getBizType());
        detail.setUserId(orderDO.getUserId());
        detail.setPerformStatus(SubOrderPerformStatusEnum.INIT);
        detail.setPayAmountFen(orderDO.getPaymentInfo().getPayAmountFen());
        event.setDetail(detail);
        return event;
    }

    private TradeEventDO buildTradeEvent(MemberSubOrderDO subOrder, List<String> itemTokens, TradeEventEnum eventType, Integer periodIndex) {
        TradeEventDO event = new TradeEventDO();
        event.setEventType(eventType);
        TradeEventDetailDO detail = new TradeEventDetailDO();
        detail.setEventTime(subOrder.getUtime());
        detail.setSkuId(subOrder.getSkuId());
        detail.setTradeId(subOrder.getTradeId());
        detail.setBizType(subOrder.getBizType());
        detail.setSubTradeId(subOrder.getSubTradeId());
        detail.setUserId(subOrder.getUserId());
        detail.setPerformStatus(subOrder.getPerformStatus());
        detail.setPeriodIndex(periodIndex == null ? 1 : periodIndex);
        detail.setItemTokens(itemTokens);
        event.setDetail(detail);
        return event;
    }
}