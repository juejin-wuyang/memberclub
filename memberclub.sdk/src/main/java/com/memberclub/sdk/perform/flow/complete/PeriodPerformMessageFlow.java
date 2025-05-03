/**
 * @(#)PeriodPerformMessageFlow.java, 十二月 29, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.flow.complete;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.perform.period.PeriodPerformContext;
import com.memberclub.sdk.event.trade.service.domain.TradeEventDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 * 周期履约消息构建
 */
@Service
public class PeriodPerformMessageFlow extends FlowNode<PeriodPerformContext> {

    @Autowired
    private TradeEventDomainService tradeEventDomainService;

    @Override
    public void process(PeriodPerformContext context) {

    }

    @Override
    public void success(PeriodPerformContext context) {
        tradeEventDomainService.publishEventOnPeriodPerformSuccess(context);
    }
}