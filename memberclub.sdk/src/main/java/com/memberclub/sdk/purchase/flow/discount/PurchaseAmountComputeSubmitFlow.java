package com.memberclub.sdk.purchase.flow.discount;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.sdk.purchase.service.AmountComputeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurchaseAmountComputeSubmitFlow extends FlowNode<PurchaseSubmitContext> {

    @Autowired
    private AmountComputeService amountComputeService;

    @Override
    public void process(PurchaseSubmitContext context) {
        amountComputeService.amountCompute(context);
    }
}
