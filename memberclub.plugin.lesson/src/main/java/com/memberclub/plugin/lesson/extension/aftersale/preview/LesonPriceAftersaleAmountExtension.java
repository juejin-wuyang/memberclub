package com.memberclub.plugin.lesson.extension.aftersale.preview;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.aftersale.contant.RefundWayEnum;
import com.memberclub.domain.context.aftersale.preview.AftersalePreviewContext;
import com.memberclub.domain.context.aftersale.preview.ItemUsage;
import com.memberclub.sdk.aftersale.extension.preview.AftersaleAmountExtension;
import com.memberclub.sdk.aftersale.service.domain.AftersaleAmountService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@ExtensionProvider(desc = "价格除法计算售后金额", bizScenes = {
        @Route(bizType = BizTypeEnum.LESSON, scenes = SceneEnum.DEFAULT_SCENE)
})
public class LesonPriceAftersaleAmountExtension implements AftersaleAmountExtension {

    @Autowired
    private AftersaleAmountService aftersaleAmountService;

    @Override
    public int calculteRecommendRefundPrice(AftersalePreviewContext context, Map<String, ItemUsage> batchCode2ItemUsageMap) {
        return aftersaleAmountService.payPriceDividedUsed(context.getCurrentSubOrderDO().getActPriceFen(), batchCode2ItemUsageMap);
    }

    @Override
    public void calculateUsageTypeByAmount(AftersalePreviewContext context) {
        aftersaleAmountService.calculateUsageTypeByAmount(context);
    }

    @Override
    public RefundWayEnum calculateRefundWay(AftersalePreviewContext context) {
        return aftersaleAmountService.calculateRefundWaySupportPortionRefund(context);
    }
}
