package com.memberclub.sdk.payment.extension.impl;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.dataobject.payment.PayNodeTypeEnum;
import com.memberclub.domain.dataobject.payment.PayOnlineTypeEnum;
import com.memberclub.domain.dataobject.payment.PayStatusEnum;
import com.memberclub.domain.dataobject.payment.PaymentDO;
import com.memberclub.domain.dataobject.payment.context.PaymentNotifyContext;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.sdk.common.SwitchEnum;
import com.memberclub.sdk.payment.extension.PaymentExtension;
import org.apache.commons.lang3.ObjectUtils;

import static com.memberclub.sdk.common.SwitchEnum.PAY_AMOUNT_STRICT_VALIDATE;

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
    public void validateAmount4Notify(PaymentNotifyContext context) {
        if (context.getCmd().getPayAmountFen() <= 0) {
            throw ResultCode.PAY_EXCEPTION.newException("支付异常，支付金额小于0");
        }
        if (PAY_AMOUNT_STRICT_VALIDATE.getBoolean(context.getBizType().getCode())) {
            if (ObjectUtils.notEqual(context.getCmd().getPayAmountFen(), context.getMemberOrderDO().getActPriceFen())) {
                throw ResultCode.PAY_EXCEPTION.newException(
                        String.format("支付异常，支付金额和订单金额不一致:payAmountFen: %s, orderAmountFen:%s",
                                context.getCmd().getPayAmountFen(), context.getMemberOrderDO().getActPriceFen()));
            }
        }
    }

    @Override
    public void validate4BizException(PaymentNotifyContext context) {

    }

    @Override
    public void initializePayment(PurchaseSubmitContext context) {
        PaymentDO paymentDO = context.initPayment();

        initializeBasic(paymentDO);

        initializeProductInfo(context, paymentDO);

        initializePayExpireTime(context);
    }

    @Override
    public void initializePaymentRefundOrder(AfterSaleApplyContext context) {

    }
}
