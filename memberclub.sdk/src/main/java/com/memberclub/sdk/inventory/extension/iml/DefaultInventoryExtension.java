/**
 * @(#)DefaultInventoryExtension.java, 一月 29, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.inventory.extension.iml;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.flow.FlowChain;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.inventory.InventoryOpContext;
import com.memberclub.domain.context.inventory.InventoryOpTypeEnum;
import com.memberclub.domain.context.inventory.InventorySkuOpDO;
import com.memberclub.domain.dataobject.sku.InventoryTypeEnum;
import com.memberclub.domain.entity.inventory.Inventory;
import com.memberclub.sdk.inventory.extension.InventoryExtension;
import com.memberclub.sdk.inventory.flow.*;

import javax.annotation.PostConstruct;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "库存扩展点", bizScenes = {@Route(
        bizType = BizTypeEnum.DEFAULT
)})
public class DefaultInventoryExtension implements InventoryExtension {

    private FlowChain<InventoryOpContext> inventoryDecrementFlowChain = null;

    private FlowChain<InventoryOpContext> inventoryDecrementValidateChain = null;

    private FlowChain<InventoryOpContext> inventoryRollbackFlowChain = null;

    @PostConstruct
    public void init() {
        inventoryDecrementFlowChain = FlowChain.newChain(InventoryOpContext.class)
                .addNode(InventoryFilteredFlow.class)
                .addNode(InventoryOperateFlow.class)
        ;


        inventoryDecrementValidateChain = FlowChain.newChain(InventoryOpContext.class)
                .addNode(InventoryFilteredFlow.class)
                .addNode(InventoryValidateFlow.class)
        ;


        inventoryRollbackFlowChain = FlowChain.newChain(InventoryOpContext.class)
                .addNode(InventoryRecordsQueryFlow.class)
                .addNode(InventoryIdemValidateFlow.class)
                .addNode(InventoryOperateFlow.class)
        ;
    }

    @Override
    public void validate(InventoryOpContext context) {
        inventoryDecrementValidateChain.execute(context);
    }

    @Override
    public void operate(InventoryOpContext context) {
        if (context.getCmd().getOpType() == InventoryOpTypeEnum.DECREMENT) {
            inventoryDecrementFlowChain.execute(context);
        } else {
            inventoryRollbackFlowChain.execute(context);
        }

    }

    @Override
    public void buildSubKey(InventoryOpContext context) {
        for (InventorySkuOpDO sku : context.getCmd().getSkus()) {
            //总量库存

            //如果使用时间,需要使用 context.getcmd.getSubmitTime()
            sku.setSubKey(Inventory.buildSubKey(InventoryTypeEnum.TOTAL));
            //如果有其他库存
        }
    }
}