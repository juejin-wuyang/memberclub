/**
 * @(#)TradeEventDomainExtension.java, 一月 12, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.event.trade.extension;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.SubOrderPerformContext;
import com.memberclub.domain.context.perform.period.PeriodPerformContext;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.domain.context.perform.reverse.SubOrderReversePerformContext;
import com.memberclub.domain.context.purchase.cancel.PurchaseCancelContext;
import com.memberclub.domain.dataobject.event.trade.TradeEventDO;
import com.memberclub.domain.dataobject.payment.context.PaymentNotifyContext;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;

/**
 * @author wuyang
 */
@ExtensionConfig(desc = "TradeEvent 事件构建扩展点", type = ExtensionType.COMMON, must = true)
public interface TradeEventDomainExtension extends BaseExtension {

    public String onPurchaseCancelSuccessForSubOrder(PurchaseCancelContext cancelContext, MemberOrderDO memberOrderDO,
                                                     MemberSubOrderDO subOrder,
                                                     TradeEventDO event);

    public String onPerformSuccessForSubOrder(PerformContext performContext,
                                              SubOrderPerformContext subOrderPerformContext,
                                              MemberSubOrderDO subOrder,
                                              TradeEventDO event);


    public String onPeriodPerformSuccessForSubOrder(PeriodPerformContext context, TradeEventDO event);

    public String onReversePerformSuccessForSubOrder(ReversePerformContext context,
                                                     SubOrderReversePerformContext subOrderReversePerformContext,
                                                     MemberSubOrderDO subOrder, TradeEventDO event);

    public String onRefundSuccessForSubOrder(AfterSaleApplyContext context,
                                             MemberSubOrderDO subOrder,
                                             TradeEventDO event);

    String onPaySuccess(PaymentNotifyContext context, TradeEventDO event);
}