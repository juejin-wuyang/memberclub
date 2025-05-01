/**
 * @(#)PurchaseOperateInventoryFlow.java, 一月 29, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.purchase.flow;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.retry.Retryable;
import com.memberclub.domain.context.inventory.InventoryOpCmd;
import com.memberclub.domain.context.inventory.InventoryOpResponse;
import com.memberclub.domain.context.inventory.InventoryOpTypeEnum;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.sdk.inventory.service.InventoryBizService;
import com.memberclub.sdk.purchase.service.PurchaseDataObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class PurchaseInventoryOperateSubmitFlow extends FlowNode<PurchaseSubmitContext> {

    @Autowired
    private InventoryBizService inventoryBizService;

    @Autowired
    private PurchaseInventoryOperateSubmitFlow purchaseInventoryOperateSubmitFlow;

    @Autowired
    private PurchaseDataObjectFactory purchaseDataObjectFactory;

    @Override
    public void process(PurchaseSubmitContext context) {
        InventoryOpCmd cmd = purchaseDataObjectFactory.toInventoryOpCmd(context, InventoryOpTypeEnum.DECREMENT);

        InventoryOpResponse response = inventoryBizService.operateSkuInventory(cmd);
        if (response.isNeedRetry()) {
            CommonLog.error("库存扣减操作需要重试 response:{} cmd:{}", response, cmd);
            purchaseInventoryOperateSubmitFlow.retryOperate(cmd);
        }

        if (!response.isSuccess()) {
            throw ResultCode.COMMON_ORDER_SUBMIT_ERROR.newException("扣减库存失败", response.getE());
        }
    }

    @Override
    public void rollback(PurchaseSubmitContext context, Exception e) {
        InventoryOpCmd inventoryOpCmd = purchaseDataObjectFactory.toInventoryOpCmd(context, InventoryOpTypeEnum.ROLLBACK);

        InventoryOpResponse response = inventoryBizService.operateSkuInventory(inventoryOpCmd);
        if (response.isNeedRetry()) {
            CommonLog.error("库存回补操作需要重试 response:{} cmd:{}", response, inventoryOpCmd);
            purchaseInventoryOperateSubmitFlow.retryOperate(inventoryOpCmd);
        }

        if (!response.isSuccess()) {
            throw ResultCode.COMMON_ORDER_SUBMIT_ERROR.newException("回补库存失败", response.getE());
        }
    }

    @Retryable(throwException = false)
    public void retryOperate(InventoryOpCmd cmd) {
        inventoryBizService.operateSkuInventory(cmd);
    }
}