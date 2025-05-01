/**
 * @(#)ReversePerformMemberOrderFLow.java, 一月 05, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.flow.reverse;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.sdk.memberorder.domain.MemberOrderDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class MemberOrderOnReverseSuccessFlow extends FlowNode<ReversePerformContext> {

    @Autowired
    private MemberOrderDomainService memberOrderDomainService;

    @Override
    public void process(ReversePerformContext context) {

    }

    @Override
    public void success(ReversePerformContext context) {
        memberOrderDomainService.onReversePerformSuccess(context);
    }
}