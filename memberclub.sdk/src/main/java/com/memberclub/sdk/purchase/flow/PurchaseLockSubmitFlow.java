/**
 * @(#)PurchaseSubmitLockFlow.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.purchase.flow;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.sdk.perform.service.domain.MemberTradeLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class PurchaseLockSubmitFlow extends FlowNode<PurchaseSubmitContext> {

    @Autowired
    private MemberTradeLockService memberTradeLockService;

    @Override
    public void process(PurchaseSubmitContext context) {
        memberTradeLockService.lockOnPrePurchase(context);
    }

    @Override
    public void callback(PurchaseSubmitContext context, Exception e) {
        if (e != null) {
            memberTradeLockService.unlockOnPurchaseFail(context);
        } else {
            memberTradeLockService.unlockOnPurchaseSuccess(context);
        }
    }
}