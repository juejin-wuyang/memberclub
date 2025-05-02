/**
 * @(#)PurchaseCancelOrderFlow.java, 二月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.purchase.flow.cancel;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.purchase.cancel.PurchaseCancelContext;
import com.memberclub.sdk.memberorder.MemberOrderDataObjectBuildFactory;
import com.memberclub.sdk.memberorder.domain.MemberOrderDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class PurchaseOrderCancelFlow extends FlowNode<PurchaseCancelContext> {

    @Autowired
    private MemberOrderDomainService memberOrderDomainService;

    @Autowired
    private MemberOrderDataObjectBuildFactory memberOrderDataObjectBuildFactory;

    @Override
    public void process(PurchaseCancelContext context) {
    }

    @Override
    public void success(PurchaseCancelContext context) {
        memberOrderDataObjectBuildFactory.onSubmitCancel(context);
        memberOrderDomainService.onSubmitCancel(context, context.getMemberOrder());
    }
}