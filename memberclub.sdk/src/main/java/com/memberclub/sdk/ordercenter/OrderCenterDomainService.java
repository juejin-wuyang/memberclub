/**
 * @(#)OrderDomainService.java, 一月 05, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.ordercenter;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.dataobject.purchase.facade.CommonOrderRefundResult;
import com.memberclub.domain.dataobject.purchase.facade.CommonOrderSubmitResult;
import com.memberclub.domain.dataobject.purchase.facade.SkuBuyResultDO;
import com.memberclub.sdk.purchase.extension.CommonOrderExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class OrderCenterDomainService {
    @Autowired
    private ExtensionManager extensionManager;

    public void refundOrder(AfterSaleApplyContext context) {
        context.setOrderRefundPriceFen(context.getPreviewContext().getRecommendRefundPrice());
        // TODO: 2025/1/5
        CommonOrderExtension extension = extensionManager.getExtension(
                BizScene.of(context.getCmd().getBizType()), CommonOrderExtension.class);

        CommonOrderRefundResult result = extension.refund(context);
        context.getAftersaleOrderDO().getExtra().setOrderRefundId(result.getOrderRefundId());
        context.getAftersaleOrderDO().setActRefundPriceFen(context.getOrderRefundPriceFen());
        context.setPayOrderRefundInvokeSuccess(true);
        CommonLog.info("调用订单退款 orderRefundId:{}", result.getOrderRefundId());
    }


    public void submitOrder(PurchaseSubmitContext context) {
        CommonOrderExtension extension = extensionManager.getExtension(
                context.toDefaultBizScene(), CommonOrderExtension.class);

        extension.onPreSubmit(context);

        CommonOrderSubmitResult result = extension.doSubmit(context);
        context.getMemberOrder().getOrderInfo().setOrderId(result.getOrderId());
        context.getMemberOrder().setActPriceFen(result.getActPriceFen());
        for (SkuBuyResultDO skuBuyResult : result.getSkuBuyResults()) {
            for (MemberSubOrderDO subOrder : context.getMemberOrder().getSubOrders()) {
                if (skuBuyResult.getSkuId() == subOrder.getSkuId()) {
                    subOrder.setOrderId(result.getOrderId());
                    subOrder.setActPriceFen(skuBuyResult.getActPriceFen());
                }
            }
        }
    }
}