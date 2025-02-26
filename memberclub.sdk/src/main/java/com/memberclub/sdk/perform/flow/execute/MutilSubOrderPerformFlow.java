package com.memberclub.sdk.perform.flow.execute;

import com.memberclub.common.flow.SubFlowNode;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.SubOrderPerformContext;
import com.memberclub.sdk.memberorder.domain.MemberSubOrderDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MutilSubOrderPerformFlow extends SubFlowNode<PerformContext, PerformContext> {

    @Autowired
    private MemberSubOrderDomainService memberSubOrderDomainService;

    @Override
    public void process(PerformContext context) {
        for (SubOrderPerformContext subOrderPerformContext : context.getSubOrderPerformContexts()) {
            context.setCurrentSubOrderPerformContext(subOrderPerformContext);
            getSubChain().execute(context);
        }
    }


    @Override
    public void success(PerformContext context) {
    }
}
