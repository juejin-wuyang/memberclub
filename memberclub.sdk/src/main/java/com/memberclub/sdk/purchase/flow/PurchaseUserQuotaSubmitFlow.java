/**
 * @(#)PurchaseUserQuotaFlow.java, 一月 30, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.purchase.flow;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.sdk.quota.service.QuotaDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class PurchaseUserQuotaSubmitFlow extends FlowNode<PurchaseSubmitContext> {

    @Autowired
    private QuotaDomainService quotaDomainService;

    @Override
    public void process(PurchaseSubmitContext context) {
        quotaDomainService.validate(context);
    }

    @Override
    public void success(PurchaseSubmitContext context) {
        quotaDomainService.onSubmitSuccess(context);
    }
}