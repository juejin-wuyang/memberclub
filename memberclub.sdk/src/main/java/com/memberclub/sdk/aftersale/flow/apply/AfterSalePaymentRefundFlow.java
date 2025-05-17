package com.memberclub.sdk.aftersale.flow.apply;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.sdk.aftersale.service.domain.AfterSaleDomainService;
import com.memberclub.sdk.memberorder.domain.MemberOrderDomainService;
import com.memberclub.sdk.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AfterSalePaymentRefundFlow extends FlowNode<AfterSaleApplyContext> {

    @Autowired
    PaymentService paymentService;

    @Autowired
    private AfterSaleDomainService afterSaleDomainService;

    @Autowired
    private MemberOrderDomainService memberOrderDomainService;

    @Override
    public void process(AfterSaleApplyContext afterSaleApplyContext) {
        paymentService.paymentRefund(afterSaleApplyContext);

        afterSaleDomainService.onPayRefundSuccess(afterSaleApplyContext);
        memberOrderDomainService.onPayRefundSuccess(afterSaleApplyContext);
    }
}
