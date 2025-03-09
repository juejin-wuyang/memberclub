/**
 * @(#)PreFinanceBuildMemberAssetsFlow.java, 一月 25, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.prefinance.flow;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.prefinance.PreFinanceContext;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.sdk.perform.service.domain.MemberPerformItemDomainService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Service
public class PreFinanceBuildPerformItemsFlow extends FlowNode<PreFinanceContext> {

    @Autowired
    private MemberPerformItemDomainService memberPerformItemDomainService;

    @Override
    public void process(PreFinanceContext preFinanceContext) {
        if (CollectionUtils.isNotEmpty(preFinanceContext.getEvent().getDetail().getItemTokens())) {

            List<MemberPerformItemDO> items = memberPerformItemDomainService.batchQueryItems(
                    preFinanceContext.getUserId(), preFinanceContext.getEvent().getDetail().getItemTokens());
            preFinanceContext.setPerformItems(items);
        }
    }
}