package com.memberclub.sdk.aftersale.extension.preview;

import com.google.common.collect.Maps;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.aftersale.contant.UsageTypeEnum;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewContext;
import com.memberclub.domain.context.aftersale.preview.ItemUsage;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ExtensionProvider(desc = "默认资格类权益实时查询计算使用情况", bizScenes = {
        @Route(bizType = BizTypeEnum.DEMO_MEMBER, scenes = {SceneEnum.RIGHT_TYPE_SCENE_MEMBER_DISCOUNT_PRICE})
})
public class DefaultQualificationRealtimeCalculateUsageExtension implements RealtimeCalculateUsageExtension {

    @Override
    public Map<String, ItemUsage> calculateItemUsage(AfterSalePreviewContext context) {
        List<String> assetBatchCodes = context.getCurrentPerformItemsGroupByRightType()
                .stream()
                .map(MemberPerformItemDO::getBatchCode)
                .collect(Collectors.toList());
        Map<String, ItemUsage> batchCode2ItemUsage = Maps.newHashMap();
        for (String assetBatchCode : assetBatchCodes) {
            //需要资格类权益核销数据，获取当前已使用的金额

            ItemUsage usage = new ItemUsage();
            usage.setUsageType(UsageTypeEnum.UNUSE);
            usage.setTotalPrice(0);
            usage.setUsedPrice(0);
            batchCode2ItemUsage.put(assetBatchCode, usage);
        }

        return batchCode2ItemUsage;
    }
}