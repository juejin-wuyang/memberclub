/**
 * @(#)PreFinanceQueryTradeDataFlow.java, 一月 25, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.prefinance.flow;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.prefinance.PreFinanceContext;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.sdk.memberorder.domain.MemberOrderDomainService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class PreFinanceMemberOrderInfoBuildFlow extends FlowNode<PreFinanceContext> {

    @Autowired
    private MemberOrderDomainService memberOrderDomainService;

    @Override
    public void process(PreFinanceContext context) {
        MemberOrderDO order = memberOrderDomainService.getMemberOrderDO(context.getUserId(), context.getTradeId(), Long.valueOf(context.getSubTradeId()));
        context.setMemberOrder(order);
        context.setSubOrder(CollectionUtils.isNotEmpty(order.getSubOrders()) ? order.getSubOrders().get(0) : null);
    }
}