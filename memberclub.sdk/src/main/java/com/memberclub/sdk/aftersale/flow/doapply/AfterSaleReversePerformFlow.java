/**
 * @(#)AftersaleReversePerformFlow.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.flow.doapply;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.sdk.aftersale.service.domain.AfterSaleDomainService;
import com.memberclub.sdk.perform.service.PerformBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class AfterSaleReversePerformFlow extends FlowNode<AfterSaleApplyContext> {

    @Autowired
    private PerformBizService performBizService;

    @Autowired
    private AfterSaleDomainService aftersaleDomainService;

    @Override
    public void process(AfterSaleApplyContext context) {
        if (context.getAftersaleOrderDO().getStatus().isPerformReversed()) {
            CommonLog.info("当前售后状态已完成逆向履约,不再重复执行");
            return;
        }
        CommonLog.info("开始执行逆向履约");

        performBizService.reversePerform(context);

        aftersaleDomainService.onPerformReversed(context);
    }
}