/**
 * @(#)MemberOrderSubmitInitialFlow.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.purchase.flow;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.sdk.memberorder.MemberOrderDataObjectBuildFactory;
import com.memberclub.sdk.memberorder.domain.MemberOrderDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class PurchaseMemberOrderSubmitFlow extends FlowNode<PurchaseSubmitContext> {

    @Autowired
    private MemberOrderDataObjectBuildFactory memberOrderDataObjectBuildFactory;

    @Autowired
    private MemberOrderDomainService memberOrderDomainService;

    @Override
    public void process(PurchaseSubmitContext context) {
        MemberOrderDO memberOrderDO = memberOrderDataObjectBuildFactory.build(context);
        context.setMemberOrder(memberOrderDO);

        memberOrderDomainService.createMemberOrder(memberOrderDO);
    }


    @Override
    public void success(PurchaseSubmitContext context) {
        memberOrderDataObjectBuildFactory.onSubmitSuccess(context);
        memberOrderDomainService.onSubmitSuccess(context.getMemberOrder());
    }

    @Override
    public void rollback(PurchaseSubmitContext context, Exception e) {
        memberOrderDataObjectBuildFactory.onSubmitFail(context, e);
        memberOrderDomainService.submitFail(context.getMemberOrder());
    }
}