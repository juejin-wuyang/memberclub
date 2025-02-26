package com.memberclub.sdk.aftersale.flow.preview;

import com.memberclub.common.flow.SubFlowNode;
import com.memberclub.domain.context.aftersale.preview.AftersalePreviewContext;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import org.springframework.stereotype.Service;

@Service
public class MutilSubOrderPreviewFlow extends SubFlowNode<AftersalePreviewContext, AftersalePreviewContext> {

    @Override
    public void process(AftersalePreviewContext context) {
        for (MemberSubOrderDO subOrder : context.getSubOrders()) {
            context.setCurrentSubOrderDO(subOrder);
            getSubChain().execute(context);
        }
    }
}
