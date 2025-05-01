/**
 * @(#)InventoryQueryRecordsFlow.java, 二月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.inventory.flow;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.common.util.CollectionUtilEx;
import com.memberclub.domain.context.inventory.InventoryOpContext;
import com.memberclub.domain.context.inventory.InventoryOpTypeEnum;
import com.memberclub.domain.entity.inventory.InventoryRecord;
import com.memberclub.sdk.inventory.service.InventoryDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * author: 掘金五阳
 */
@Service
public class InventoryRecordsQueryFlow extends FlowNode<InventoryOpContext> {

    @Autowired
    private InventoryDomainService inventoryDomainService;

    @Override
    public void process(InventoryOpContext context) {
        List<InventoryRecord> records = inventoryDomainService.queryInventoryUserRecords(context.getCmd().getUserId(),
                context.getCmd().getOperateKey(), InventoryOpTypeEnum.DECREMENT.getCode());

        Map<Long, List<InventoryRecord>> skuId2InventoryRecords =
                CollectionUtilEx.groupingBy(records, InventoryRecord::getTargetId);
        context.setSkuId2InventoryRecords(skuId2InventoryRecords);
    }
}