/**
 * @(#)BuildReversePerformContextFlow.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.flow.reverse;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.domain.context.perform.reverse.SubOrderReversePerformContext;
import com.memberclub.sdk.perform.extension.reverse.BuildReverseInfoExtension;
import com.memberclub.sdk.perform.service.domain.PerformDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * author: 掘金五阳
 */
@Service
public class ReversePerformContextInitializeFlow extends FlowNode<ReversePerformContext> {

    @Autowired
    private ExtensionManager extensionManager;

    @Autowired
    private PerformDomainService performDomainService;

    @Override
    public void process(ReversePerformContext reversePerformContext) {
        Map<String, SubOrderReversePerformContext> subTradeId2SubOrderReversePerformContext =
                performDomainService.buildReverseContextMap(reversePerformContext);
        reversePerformContext.setSubTradeId2SubOrderReversePerformContext(subTradeId2SubOrderReversePerformContext);
        reversePerformContext.setMemberOrderDO(
                performDomainService.extractMemberOrderDOFromReversePerformContext(reversePerformContext));

        BuildReverseInfoExtension extension = extensionManager.getExtension(BizScene.of(reversePerformContext.getBizType()),
                BuildReverseInfoExtension.class);
        extension.buildAssets(reversePerformContext);
        extension.buildTasks(reversePerformContext);
    }
}