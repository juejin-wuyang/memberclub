package com.memberclub.starter.controller.convertor;

import com.memberclub.domain.dataobject.sku.SkuInfoDO;
import com.memberclub.sdk.util.PriceUtils;
import com.memberclub.starter.controller.vo.sku.SkuPreviewVO;

public class ManageConvertor {

    public static SkuPreviewVO toSkuPreviewVO(SkuInfoDO skuInfoDO) {
        SkuPreviewVO vo = new SkuPreviewVO();
        vo.setId(skuInfoDO.getSkuId());
        vo.setImage(skuInfoDO.getViewInfo().getDisplayImage());
        vo.setOriginPrice(PriceUtils.change2Yuan(skuInfoDO.getSaleInfo().getOriginPriceFen()));
        vo.setPrice(PriceUtils.change2Yuan(skuInfoDO.getSaleInfo().getSalePriceFen()));
        vo.setFirmId(skuInfoDO.getBizType());
        vo.setBizId(skuInfoDO.getBizType());
        vo.setDesc(skuInfoDO.getViewInfo().getDisplayDesc());
        vo.setTitle(skuInfoDO.getViewInfo().getDisplayName());
        return vo;
    }
}
