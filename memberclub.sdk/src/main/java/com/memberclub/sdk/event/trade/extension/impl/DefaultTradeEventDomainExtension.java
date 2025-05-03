/**
 * @(#)DefaultTradeEventDomainExtension.java, 一月 12, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.event.trade.extension.impl;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.util.JsonUtils;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.SubOrderPerformContext;
import com.memberclub.domain.context.perform.period.PeriodPerformContext;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.domain.context.perform.reverse.SubOrderReversePerformContext;
import com.memberclub.domain.context.purchase.cancel.PurchaseCancelContext;
import com.memberclub.domain.dataobject.event.trade.TradeEvent;
import com.memberclub.domain.dataobject.event.trade.TradeEventDO;
import com.memberclub.domain.dataobject.event.trade.TradeEventDetail;
import com.memberclub.domain.dataobject.payment.context.PaymentNotifyContext;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.infrastructure.mapstruct.CommonConvertor;
import com.memberclub.sdk.event.trade.extension.TradeEventDomainExtension;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "默认 TradeEvent 事件构建扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DEFAULT, scenes = SceneEnum.DEFAULT_SCENE)
})
public class DefaultTradeEventDomainExtension implements TradeEventDomainExtension {

    @Override
    public String onPurchaseCancelSuccessForSubOrder(PurchaseCancelContext cancelContext, MemberOrderDO memberOrderDO,
                                                     MemberSubOrderDO subOrder, TradeEventDO event) {
        return toEventValue(event);
    }

    @Override
    public String onPerformSuccessForSubOrder(PerformContext performContext,
                                              SubOrderPerformContext subOrderPerformContext,
                                              MemberSubOrderDO subOrder,
                                              TradeEventDO event) {
        return toEventValue(event);
    }

    @Override
    public String onPeriodPerformSuccessForSubOrder(PeriodPerformContext context, TradeEventDO event) {
        return toEventValue(event);
    }

    @Override
    public String onReversePerformSuccessForSubOrder(ReversePerformContext context,
                                                     SubOrderReversePerformContext subOrderReversePerformContext,
                                                     MemberSubOrderDO subOrder,
                                                     TradeEventDO event) {
        return toEventValue(event);
    }

    @Override
    public String onRefundSuccessForSubOrder(AfterSaleApplyContext context, MemberSubOrderDO subOrder, TradeEventDO event) {
        return toEventValue(event);
    }

    @Override
    public String onPaySuccess(PaymentNotifyContext context, TradeEventDO event) {
        return toEventValue(event);
    }

    public String toEventValue(TradeEventDO event) {
        TradeEvent e = new TradeEvent();
        e.setEventType(event.getEventType().getCode());
        TradeEventDetail detail = CommonConvertor.INSTANCE.toTradeEvent(event.getDetail());
        e.setDetail(detail);
        return JsonUtils.toJson(e);
    }
}