package com.memberclub.sdk.purchase.flow.payment;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.sdk.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrePaySubmitFlow extends FlowNode<PurchaseSubmitContext> {

    @Autowired
    private PaymentService paymentService;

    @Override
    public void process(PurchaseSubmitContext context) {
        paymentService.prePay(context);
    }
}
