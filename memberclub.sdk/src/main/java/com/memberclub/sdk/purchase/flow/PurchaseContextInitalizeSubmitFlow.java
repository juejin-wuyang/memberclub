/**
 * @(#)SkuInfoInitalSubmitFlow.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.purchase.flow;

import com.google.common.collect.Lists;
import com.memberclub.common.flow.FlowNode;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.context.purchase.PurchaseSkuSubmitCmd;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.dataobject.sku.SkuInfoDO;
import com.memberclub.infrastructure.sku.SkuBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Service
public class PurchaseContextInitalizeSubmitFlow extends FlowNode<PurchaseSubmitContext> {

    @Autowired
    private SkuBizService skuBizService;

    private static void initializeStartTime(PurchaseSubmitContext context) {
        context.setStartTime(TimeUtil.now());
    }

    @Override
    public void process(PurchaseSubmitContext context) {
        initializeSkuInfo(context);                 // 初始化商品信息

        initializeStartTime(context);
    }

    private void initializeSkuInfo(PurchaseSubmitContext context) {
        List<SkuInfoDO> skuInfos = Lists.newArrayList();
        for (PurchaseSkuSubmitCmd sku : context.getSubmitCmd().getSkus()) {
            SkuInfoDO skuInfoDO = skuBizService.querySku(sku.getSkuId());
            skuInfoDO.setBuyCount(sku.getBuyCount());
            skuInfos.add(skuInfoDO);
        }
        context.setSkuInfos(skuInfos);
    }


}