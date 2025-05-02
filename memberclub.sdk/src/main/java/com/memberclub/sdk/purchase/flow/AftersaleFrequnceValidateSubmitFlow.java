package com.memberclub.sdk.purchase.flow;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.sdk.quota.service.aftersale.AftersaleQuotaDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AftersaleFrequnceValidateSubmitFlow extends FlowNode<PurchaseSubmitContext> {

    @Autowired
    private AftersaleQuotaDomainService aftersaleQuotaDomainService;

    @Override
    public void process(PurchaseSubmitContext context) {
        aftersaleQuotaDomainService.validate(context);
    }
}
