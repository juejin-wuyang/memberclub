/**
 * @(#)DefaultPurchaseOrderExtraBuildExtension.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.purchase.extension.impl;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.context.purchase.cancel.PurchaseCancelContext;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.dataobject.sku.SkuInfoDO;
import com.memberclub.sdk.purchase.extension.PurchaseOrderBuildExtension;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "默认购买订单扩展属性构建", bizScenes = {
        @Route(bizType = BizTypeEnum.DEFAULT, scenes = {SceneEnum.DEFAULT_SCENE})
})
public class DefaultPurchaseOrderBuildExtension implements PurchaseOrderBuildExtension {

    @Override
    public void buildOrder(MemberOrderDO order, PurchaseSubmitContext context) {
        //order.getOrderInfo().setRelatedOrderSystemType(OrderSystemTypeEnum.ACTUAL_ORDER);
    }

    @Override
    public void buildSubOrder(MemberOrderDO order, MemberSubOrderDO subOrder,
                              PurchaseSubmitContext context, SkuInfoDO skuInfo) {
        //subOrder.setRelatedOrderSystemType(OrderSystemTypeEnum.ACTUAL_ORDER);
    }

    @Override
    public void onSubmitSuccess(MemberOrderDO order, PurchaseSubmitContext context) {

    }

    @Override
    public void onSubmitCancel(MemberOrderDO order, PurchaseCancelContext context) {

    }

    @Override
    public void onSubmitFail(MemberOrderDO order, PurchaseSubmitContext context, Exception e) {

    }
}