package com.memberclub.sdk.outer.flow;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.dataobject.outer.OuterSubmitContext;
import com.memberclub.sdk.redeem.RedeemDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedeemExchangeFlow extends FlowNode<OuterSubmitContext> {
    @Autowired
    private RedeemDomainService redeemDomainService;

    @Override
    public void process(OuterSubmitContext context) {
        redeemDomainService.validateRedeem(context);
        redeemDomainService.onPreUse(context);
    }

    @Override
    public void success(OuterSubmitContext context) {
        redeemDomainService.onUsed(context);
    }
}
