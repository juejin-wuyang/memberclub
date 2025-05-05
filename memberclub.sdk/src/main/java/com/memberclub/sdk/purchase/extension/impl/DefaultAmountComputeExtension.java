package com.memberclub.sdk.purchase.extension.impl;

import com.google.common.collect.Lists;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.dataobject.sku.SkuInfoDO;
import com.memberclub.infrastructure.amountcompute.AmountComputeProductDetailDTO;
import com.memberclub.infrastructure.amountcompute.AmountComputeRequestDTO;
import com.memberclub.infrastructure.amountcompute.AmountComputeResponseDTO;
import com.memberclub.sdk.purchase.extension.AmountComputeExtension;

import java.util.List;

@ExtensionProvider(desc = "默认金额计算扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DEFAULT)
})
public class DefaultAmountComputeExtension implements AmountComputeExtension {

    @Override
    public AmountComputeRequestDTO buildRequest(PurchaseSubmitContext context) {
        AmountComputeRequestDTO request = new AmountComputeRequestDTO();
        request.setUserId(context.getUserId());
        request.setUuid(context.getSubmitCmd().getUserInfo().getUuid());
        request.setPhone(context.getSubmitCmd().getUserInfo().getPhone());
        request.setSubmitRequest(true);
        request.setUniqueId(context.getSubmitCmd().getSubmitToken());

        List<AmountComputeProductDetailDTO> details = Lists.newArrayList();
        for (SkuInfoDO skuInfo : context.getSkuInfos()) {
            AmountComputeProductDetailDTO detail = new AmountComputeProductDetailDTO();
            detail.setCount(skuInfo.getBuyCount());
            detail.setSkuId(skuInfo.getSkuId());
            detail.setOriginPrice(skuInfo.getSaleInfo().getOriginPriceFen());
            detail.setSalePriceFen(skuInfo.getSaleInfo().getSalePriceFen());
            details.add(detail);
        }
        request.setProductDetails(details);
        return request;
    }

    @Override
    public AmountComputeResponseDTO compute4SubmitOrder(PurchaseSubmitContext context, AmountComputeRequestDTO request) {
        return null;
    }
}
