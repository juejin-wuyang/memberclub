/**
 * @(#)InventoryFilteredByRecordsFlow.java, 二月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.inventory.flow;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.flow.FlowNode;
import com.memberclub.common.flow.SkipException;
import com.memberclub.domain.context.inventory.InventoryOpContext;
import com.memberclub.sdk.inventory.service.InventoryDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class InventoryIdemValidateFlow extends FlowNode<InventoryOpContext> {

    @Autowired
    private InventoryDomainService inventoryDomainService;

    @Autowired
    private ExtensionManager extensionManager;

    @Override
    public void process(InventoryOpContext context) {
        boolean operatable = inventoryDomainService.filterByRecordsAndGetOperatable(context);
        if (!operatable) {
            throw new SkipException("无需扣减库存,跳过");
        }
    }
}