package com.memberclub.sdk.payment.extension.impl;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.dataobject.payment.PayNodeTypeEnum;
import com.memberclub.domain.dataobject.payment.PayOnlineTypeEnum;
import com.memberclub.domain.dataobject.payment.PayStatusEnum;
import com.memberclub.domain.dataobject.payment.PaymentDO;
import com.memberclub.sdk.common.SwitchEnum;
import com.memberclub.sdk.payment.extension.PaymentExtension;

@ExtensionProvider(desc = "默认支付扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DEFAULT)
})
public class DefaultPaymentExtension implements PaymentExtension {

    private static void initializeProductInfo(PurchaseSubmitContext context, PaymentDO paymentDO) {
        paymentDO.setProductName(context.getMemberOrder().getSubOrders().get(0).getExtra().getViewInfo().getDisplayName());
        paymentDO.setProductDesc(context.getMemberOrder().getSubOrders().get(0).getExtra().getViewInfo().getDisplayDesc());
    }

    private static void initializeBasic(PaymentDO paymentDO) {
        paymentDO.setMerchantId(SwitchEnum.SELF_MERCHANT_ID.getString());
        paymentDO.setPayStatus(PayStatusEnum.WAIT_PAY);
        paymentDO.setPayNodeType(PayNodeTypeEnum.IMMEDIATE);
        paymentDO.setPayOnlineType(PayOnlineTypeEnum.ONLINE);
    }

    private static void initializePayExpireTime(PurchaseSubmitContext context) {
        int waitPayTime = SwitchEnum.WAIT_PAY_TIME_SECONDS.getInt(context.getBizType().getCode()) * 1000;
        context.setPayExpireTime(context.getStartTime() + waitPayTime);
    }

    @Override
    public void initializePayment(PurchaseSubmitContext context) {
        PaymentDO paymentDO = context.initPayment();

        initializeBasic(paymentDO);

        initializeProductInfo(context, paymentDO);

        initializePayExpireTime(context);
    }
}
