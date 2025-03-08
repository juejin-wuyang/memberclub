/**
 * @(#)DefaultMemberShipReverseExtension.java, 二月 02, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.extension.reverse.impl;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.perform.reverse.AssetsReverseResponse;
import com.memberclub.domain.context.perform.reverse.PerformItemReverseInfo;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.domain.context.perform.reverse.SubOrderReversePerformContext;
import com.memberclub.domain.dataobject.membership.MemberShipDO;
import com.memberclub.sdk.membership.service.MemberShipDomainService;
import com.memberclub.sdk.perform.extension.reverse.RightsReverseExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "默认会员资格逆向扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DEMO_MEMBER, scenes = {
                SceneEnum.RIGHT_TYPE_SCENE_MEMBERSHIP,
                SceneEnum.RIGHT_TYPE_SCENE_MEMBER_DISCOUNT_PRICE
        })
})
public class DefaultShipRightsReverseExtension implements RightsReverseExtension {
    @Autowired
    private MemberShipDomainService memberShipDomainService;

    @Override
    public AssetsReverseResponse reverse(ReversePerformContext context,
                                         SubOrderReversePerformContext reverseInfo,
                                         List<PerformItemReverseInfo> items) {
        AssetsReverseResponse response = new AssetsReverseResponse();
        for (PerformItemReverseInfo item : items) {
            MemberShipDO memberShip = memberShipDomainService.getMemberShipDO(context.getUserId(), item.getBatchCode());
            memberShipDomainService.cancel(memberShip);
        }

        response.setSuccess(true);
        return response;
    }
}