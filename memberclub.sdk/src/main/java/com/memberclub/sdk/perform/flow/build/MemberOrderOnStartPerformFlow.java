/**
 * @(#)StartPerformUpdteMemberOrderFlow.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.flow.build;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.sdk.memberorder.domain.MemberOrderDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class MemberOrderOnStartPerformFlow extends FlowNode<PerformContext> {

    @Autowired
    private MemberOrderDomainService memberOrderDomainService;

    @Override
    public void process(PerformContext context) {
        MemberOrderDO memberOrder = memberOrderDomainService.getMemberOrderDO(context.getUserId(), context.getTradeId());
        if (memberOrder == null) {
            throw ResultCode.PARAM_VALID.newException("未查询到订单无法履约");
        }
        context.initialize(memberOrder);

        int count = memberOrderDomainService.onStartPerform(context);
        context.setMemberOrderStartPerformUpdateCount(count);
    }
}