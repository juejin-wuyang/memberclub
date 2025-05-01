package com.memberclub.sdk.perform.flow.reverse;

import com.memberclub.common.flow.SubFlowNode;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.domain.context.perform.reverse.SubOrderReversePerformContext;
import org.springframework.stereotype.Service;

@Service
public class ReversePerform4MutilSubOrderFlow extends SubFlowNode<ReversePerformContext, ReversePerformContext> {

    @Override
    public void process(ReversePerformContext context) {
        for (SubOrderReversePerformContext subOrderReversePerformContext : context.getSubTradeId2SubOrderReversePerformContext().values()) {
            context.setCurrentSubOrderReversePerformContext(subOrderReversePerformContext);
            getSubChain().execute(context);
        }
    }

    @Override
    public void success(ReversePerformContext context) {

    }
}
