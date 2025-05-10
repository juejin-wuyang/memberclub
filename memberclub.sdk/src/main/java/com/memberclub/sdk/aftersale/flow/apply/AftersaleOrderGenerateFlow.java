/**
 * @(#)AftersaleGenerateOrderFlow.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.flow.apply;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.dataobject.aftersale.AftersaleOrderDO;
import com.memberclub.infrastructure.id.IdTypeEnum;
import com.memberclub.sdk.aftersale.extension.apply.AfterSaleApplyExtension;
import com.memberclub.sdk.aftersale.service.domain.AfterSaleDomainService;
import com.memberclub.sdk.common.IdGeneratorDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class AftersaleOrderGenerateFlow extends FlowNode<AfterSaleApplyContext> {
    @Autowired
    private AfterSaleDomainService aftersaleDomainService;

    @Autowired
    private ExtensionManager extensionManager;


    @Autowired
    private IdGeneratorDomainService idGeneratorDomainService;

    @Override
    public void process(AfterSaleApplyContext context) {
        AftersaleOrderDO orderDO = aftersaleDomainService.generateOrder(context);
        context.setAftersaleOrderDO(orderDO);

        //todo 如何不重复生成售后ID
        Long aftersaleOrderId = idGeneratorDomainService.generateId(IdTypeEnum.AFTERSALE_ORDER_ID);
        orderDO.setId(aftersaleOrderId);

        extensionManager.getExtension(BizScene.of(context.getCmd().getBizType(), context.getScene()),
                AfterSaleApplyExtension.class).customBuildAftersaleOrder(context, orderDO);
    }
}