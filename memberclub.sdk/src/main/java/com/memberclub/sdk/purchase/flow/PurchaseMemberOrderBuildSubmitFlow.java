package com.memberclub.sdk.purchase.flow;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.sdk.memberorder.MemberOrderDataObjectBuildFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurchaseMemberOrderBuildSubmitFlow extends FlowNode<PurchaseSubmitContext> {

    @Autowired
    private MemberOrderDataObjectBuildFactory memberOrderDataObjectBuildFactory;

    @Override
    public void process(PurchaseSubmitContext context) {
        MemberOrderDO memberOrderDO = memberOrderDataObjectBuildFactory.build(context);
        context.setMemberOrder(memberOrderDO);
    }
}
