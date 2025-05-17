/**
 * @(#)CheckMemberOrderPerformedFlow.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.flow.build;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.common.RetrySourceEunm;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.purchase.common.MemberOrderStatusEnum;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.sdk.common.Monitor;
import com.memberclub.sdk.memberorder.domain.MemberOrderDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class MemberOrderPerformCheckFlow extends FlowNode<PerformContext> {


    @Autowired
    private MemberOrderDomainService memberOrderDomainService;


    @Override
    public void process(PerformContext context) {
        MemberOrderDO memberOrder = context.getMemberOrder();
        if (context.getRetrySource() == RetrySourceEunm.UPSTREAM_RETRY) {
            context.setSkipPerform(true);
        }

        if (context.getRetrySource() == RetrySourceEunm.SELF_RETRY) {
            if (memberOrder.isPerformed()) {
                context.setSkipPerform(true);
            }
            if (!MemberOrderStatusEnum.isPerformEnabled(memberOrder.getStatus().getCode())) {
                CommonLog.error("当前履约状态不允许再次履约 status", memberOrder.getStatus());
                Monitor.PERFORM_EXECUTE.counter(context.getBizType().getCode(), "curr_status_cant_perform", memberOrder.getStatus());
                throw ResultCode.CAN_NOT_PERFORM_RETRY.newException();
            }
        }

        if (context.isSkipPerform() && memberOrder != null) {
            // TODO: 2024/12/15 构建返回值
        }

    }
}