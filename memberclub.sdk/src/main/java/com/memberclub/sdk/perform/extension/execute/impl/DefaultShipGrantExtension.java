/**
 * @(#)DefaultMemberShipGrantExtension.java, 二月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.extension.execute.impl;

import com.google.common.collect.Maps;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.perform.PerformItemContext;
import com.memberclub.domain.context.perform.execute.ItemGrantResult;
import com.memberclub.domain.context.perform.execute.ItemGroupGrantResult;
import com.memberclub.domain.dataobject.membership.MemberShipDO;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.sdk.membership.service.MemberShipDataObjectFactory;
import com.memberclub.sdk.membership.service.MemberShipDomainService;
import com.memberclub.sdk.perform.extension.execute.MemberRightsGrantExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "会员资格类权益发放扩展点 默认实现", bizScenes = {
        @Route(bizType = BizTypeEnum.DEMO_MEMBER, scenes = {
                SceneEnum.RIGHT_TYPE_SCENE_MEMBERSHIP, SceneEnum.RIGHT_TYPE_SCENE_MEMBER_DISCOUNT_PRICE
        }),
})
public class DefaultShipGrantExtension implements MemberRightsGrantExtension {

    @Autowired
    private MemberShipDomainService memberShipDomainService;

    @Autowired
    private MemberShipDataObjectFactory memberShipDataObjectFactory;

    @Override
    public ItemGroupGrantResult grant(PerformItemContext context, List<MemberPerformItemDO> items) {
        ItemGroupGrantResult result = new ItemGroupGrantResult();
        Map<String, ItemGrantResult> grantMap = Maps.newHashMap();
        result.setGrantMap(grantMap);
        for (MemberPerformItemDO item : items) {
            MemberShipDO memberShipDO = memberShipDataObjectFactory.buildMemberShipDO(context, item);

            memberShipDomainService.grant(memberShipDO);

            ItemGrantResult itemGrantResult = new ItemGrantResult();
            itemGrantResult.setBatchCode(memberShipDO.getGrantCode());
            grantMap.put(memberShipDO.getGrantCode(), itemGrantResult);
        }

        return result;
    }

}