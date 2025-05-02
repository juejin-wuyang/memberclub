package com.memberclub.sdk.payment.extension;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;

@ExtensionConfig(desc = "支付扩展点", must = false, type = ExtensionType.PURCHASE)
public interface PaymentExtension extends BaseExtension {

    public void initializePayment(PurchaseSubmitContext context);
}
