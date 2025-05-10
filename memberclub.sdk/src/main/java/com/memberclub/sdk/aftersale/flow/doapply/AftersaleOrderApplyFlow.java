/**
 * @(#)AftersaleOrderFlow.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.flow.doapply;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.dataobject.aftersale.AftersaleOrderDO;
import com.memberclub.sdk.aftersale.service.domain.AfterSaleDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class AftersaleOrderApplyFlow extends FlowNode<AfterSaleApplyContext> {

    @Autowired
    private AfterSaleDomainService aftersaleDomainService;

    @Override
    public void process(AfterSaleApplyContext context) {
        AftersaleOrderDO orderDO = aftersaleDomainService.queryAftersaleOrder(
                context.getCmd().getUserId(), context.getAftersaleOrderDO().getId());
        if (orderDO != null) {
            context.setAftersaleOrderDO(orderDO);
        } else {
            aftersaleDomainService.createAfterSaleOrder(context.getAftersaleOrderDO());
        }
    }

    @Override
    public void success(AfterSaleApplyContext context) {
        aftersaleDomainService.onAftersaleSuccess(context, context.getAftersaleOrderDO());
    }

    @Override
    public void rollback(AfterSaleApplyContext context, Exception e) {

    }
}