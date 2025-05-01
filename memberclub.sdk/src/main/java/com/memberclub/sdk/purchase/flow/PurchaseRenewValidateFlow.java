package com.memberclub.sdk.purchase.flow;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.sdk.purchase.service.RenewDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurchaseRenewValidateFlow extends FlowNode<PurchaseSubmitContext> {

    @Autowired
    private RenewDomainService renewDomainService;

    @Override
    public void process(PurchaseSubmitContext context) {
        renewDomainService.buildNonExpiredOrders(context);

        renewDomainService.validateRenewTimes(context);

        renewDomainService.generateStartTime4RenewOrder(context);
    }
}
