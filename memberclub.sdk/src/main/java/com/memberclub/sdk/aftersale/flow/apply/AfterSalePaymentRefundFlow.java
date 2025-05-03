package com.memberclub.sdk.aftersale.flow.apply;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.sdk.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AfterSalePaymentRefundFlow extends FlowNode<AfterSaleApplyContext> {

    @Autowired
    PaymentService paymentService;

    @Override
    public void process(AfterSaleApplyContext afterSaleApplyContext) {
        paymentService.paymentRefund(afterSaleApplyContext);
    }
}
