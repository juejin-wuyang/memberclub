/**
 * @(#)AftersaleDoApplyFlow.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.flow.apply;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.common.util.ApplicationContextUtils;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.sdk.aftersale.service.domain.AfterSaleDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author: 掘金五阳
 */
@Service
public class AfterSaleAsyncExecuteFlow extends FlowNode<AfterSaleApplyContext> {

    @Autowired
    private AfterSaleDomainService afterSaleDomainService;

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    public void process(AfterSaleApplyContext context) {
        if (ApplicationContextUtils.isUnitTest()) {
            afterSaleDomainService.execute(context.getExecuteCmd());
            return;
        }
        executorService.submit(() -> {
            afterSaleDomainService.execute(context.getExecuteCmd());
        });
    }
}