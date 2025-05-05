package com.memberclub.sdk.purchase.extension;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.infrastructure.amountcompute.AmountComputeRequestDTO;
import com.memberclub.infrastructure.amountcompute.AmountComputeResponseDTO;

@ExtensionConfig(desc = "金额计算扩展点", must = true, type = ExtensionType.PURCHASE)
public interface AmountComputeExtension extends BaseExtension {

    public AmountComputeRequestDTO buildRequest(PurchaseSubmitContext context);

    public AmountComputeResponseDTO compute4SubmitOrder(PurchaseSubmitContext context,
                                                        AmountComputeRequestDTO request);
}
