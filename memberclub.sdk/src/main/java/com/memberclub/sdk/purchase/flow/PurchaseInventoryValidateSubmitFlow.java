/**
 * @(#)PurchaseCheckInventoryFlow.java, 一月 29, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.purchase.flow;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.inventory.InventoryOpCmd;
import com.memberclub.domain.context.inventory.InventoryOpTypeEnum;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.exception.MemberException;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.sdk.inventory.service.InventoryBizService;
import com.memberclub.sdk.purchase.service.PurchaseDataObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class PurchaseInventoryValidateSubmitFlow extends FlowNode<PurchaseSubmitContext> {

    @Autowired
    private InventoryBizService inventoryBizService;

    @Autowired
    private PurchaseDataObjectFactory purchaseDataObjectFactory;


    @Override
    public void process(PurchaseSubmitContext context) {
        InventoryOpCmd cmd = purchaseDataObjectFactory.toInventoryOpCmdOnValidate(context, InventoryOpTypeEnum.DECREMENT);
        try {
            inventoryBizService.validate(cmd);
        } catch (MemberException e) {
            throw e;
        } catch (Exception e) {
            throw ResultCode.INVENTORY_LACKING.newException("库存校验异常", e);
        }
    }
}