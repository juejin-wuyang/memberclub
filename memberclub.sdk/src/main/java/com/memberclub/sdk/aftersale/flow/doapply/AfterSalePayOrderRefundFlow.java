/**
 * @(#)AftersaleRefundFlow.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.flow.doapply;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.aftersale.contant.RefundWayEnum;
import com.memberclub.sdk.aftersale.service.domain.AfterSaleDomainService;
import com.memberclub.sdk.memberorder.domain.MemberOrderDomainService;
import com.memberclub.sdk.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class AfterSalePayOrderRefundFlow extends FlowNode<AfterSaleApplyContext> {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private AfterSaleDomainService afterSaleDomainService;
    @Autowired
    private MemberOrderDomainService memberOrderDomainService;

    @Override
    public void process(AfterSaleApplyContext context) {
        if (context.getAftersaleOrderDO().getStatus().isPayOrderRefund()) {
            CommonLog.info("当前状态已完成退款,不再重复执行");
            return;
        }
        CommonLog.info("当前退款渠道为:{}", context.getPreviewContext().getRefundWay());
        if (context.getPreviewContext().getRefundWay() != RefundWayEnum.ORDER_BACKSTRACK) {
            return;
        }

        CommonLog.info("开始支付退款流程");
        paymentService.paymentRefund(context);
        context.setPayOrderRefundInvokeSuccess(true);

        afterSaleDomainService.onPayRefundSuccess(context);
        memberOrderDomainService.onPayRefundSuccess(context);
    }

    @Override
    public void success(AfterSaleApplyContext context) {
    }
}