package com.memberclub.plugin.douyinpkg.extension.aftersale.preview;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.aftersale.contant.RefundWayEnum;
import com.memberclub.domain.context.aftersale.preview.AftersalePreviewContext;
import com.memberclub.sdk.aftersale.extension.preview.impl.DefaultPriceDividedAftersaleAmountExtension;
import com.memberclub.sdk.aftersale.service.domain.AftersaleAmountService;
import org.springframework.beans.factory.annotation.Autowired;

@ExtensionProvider(desc = "抖音券包 售后金额计算扩展点", bizScenes = {@Route(bizType = BizTypeEnum.DOUYIN_COUPON_PACKAGE)})
public class DouyinPkgAfterSaleAmountExtension extends DefaultPriceDividedAftersaleAmountExtension {
    @Autowired
    private AftersaleAmountService aftersaleAmountService;

    @Override
    public RefundWayEnum calculateRefundWay(AftersalePreviewContext context) {
        return aftersaleAmountService.calculateRefundWayUnSupportPortionRefund(context);
    }
}