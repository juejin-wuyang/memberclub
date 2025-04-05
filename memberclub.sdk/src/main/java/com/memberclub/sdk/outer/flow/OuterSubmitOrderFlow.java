package com.memberclub.sdk.outer.flow;

import com.memberclub.common.flow.SubFlowNode;
import com.memberclub.domain.dataobject.outer.OuterSubmitContext;
import com.memberclub.sdk.outer.service.OuterSubmitDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OuterSubmitOrderFlow extends SubFlowNode<OuterSubmitContext, OuterSubmitContext> {

    @Autowired
    private OuterSubmitDomainService outerSubmitDomainService;

    @Override
    public void success(OuterSubmitContext context) {
        outerSubmitDomainService.onSubmitSuccess(context);
    }

    @Override
    public void rollback(OuterSubmitContext context, Exception e) {
        outerSubmitDomainService.onSubmitFail(context);
    }

    @Override
    public void process(OuterSubmitContext context) {
        outerSubmitDomainService.submitOrder(context);
    }
}
