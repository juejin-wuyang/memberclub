package com.memberclub.sdk.payment.extension;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.dataobject.payment.context.PaymentNotifyContext;

@ExtensionConfig(desc = "支付扩展点", must = false, type = ExtensionType.PURCHASE)
public interface PaymentExtension extends BaseExtension {

    void validate4BizException(PaymentNotifyContext context);

    public void initializePayment(PurchaseSubmitContext context);

    public void validateAmount4Notify(PaymentNotifyContext context);

    void initializePaymentRefundOrder(AfterSaleApplyContext context);
}
