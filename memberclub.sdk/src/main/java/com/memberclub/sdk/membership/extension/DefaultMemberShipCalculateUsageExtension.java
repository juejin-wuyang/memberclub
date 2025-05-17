/**
 * @(#)DefaultMemberShipCalculateUsageExtension.java, 二月 02, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.membership.extension;

import com.google.common.collect.Maps;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.aftersale.contant.UsageTypeEnum;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewContext;
import com.memberclub.domain.context.aftersale.preview.ItemUsage;
import com.memberclub.domain.dataobject.membership.MemberShipDO;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.sdk.aftersale.extension.preview.RealtimeCalculateUsageExtension;
import com.memberclub.sdk.membership.service.MemberShipDomainService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "实时查询计算使用情况", bizScenes = {
        @Route(bizType = BizTypeEnum.DEMO_MEMBER, scenes = {SceneEnum.RIGHT_TYPE_SCENE_MEMBERSHIP}),
})
public class DefaultMemberShipCalculateUsageExtension implements RealtimeCalculateUsageExtension {

    @Autowired
    private MemberShipDomainService memberShipDomainService;

    @Override
    public Map<String, ItemUsage> calculateItemUsage(AfterSalePreviewContext context) {
        List<String> itemTokens = context.getCurrentPerformItemsGroupByRightType()
                .stream()
                .map(MemberPerformItemDO::getItemToken)
                .collect(Collectors.toList());
        Map<String, ItemUsage> itemToken2ItemUsage = Maps.newHashMap();
        for (String itemToken : itemTokens) {
            MemberShipDO memberShipDO = memberShipDomainService.getMemberShipDO(context.getCmd().getUserId(), itemToken);
            //若视频会员等形态,可能依据使用时间决定退款金额, 然而 优惠券优惠类会员基于资产使用状态决定退款金额,因此不计算会员身份的使用状态和使用金额
            //若其他会员需要依据身份有效期计算有效期,则单独提供扩展点实现类即可!

            ItemUsage usage = new ItemUsage();
            usage.setUsageType(UsageTypeEnum.UNUSE);
            usage.setTotalPrice(0);
            usage.setUsedPrice(0);
            itemToken2ItemUsage.put(itemToken, usage);
        }

        return itemToken2ItemUsage;
    }
}