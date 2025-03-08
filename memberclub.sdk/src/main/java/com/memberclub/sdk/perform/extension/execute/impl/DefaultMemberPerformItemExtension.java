/**
 * @(#)DefaultMemberPerformItemExtension.java, 十二月 16, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.extension.execute.impl;

import com.google.common.collect.Lists;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.perform.PerformItemContext;
import com.memberclub.domain.context.perform.SubOrderPerformContext;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.domain.entity.trade.MemberPerformItem;
import com.memberclub.infrastructure.mapstruct.PerformCustomConvertor;
import com.memberclub.sdk.perform.extension.execute.MemberPerformItemExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "构建履约项扩展点,默认实现", bizScenes = {
        @Route(bizType = BizTypeEnum.DEFAULT, scenes = {SceneEnum.DEFAULT_SCENE})
})
public class DefaultMemberPerformItemExtension implements MemberPerformItemExtension {

    @Autowired
    private ExtensionManager extensionManager;

    @Override
    public List<MemberPerformItem> toMemberPerformItems(PerformItemContext performItemContext) {
        List<MemberPerformItem> items = Lists.newArrayList();
        for (MemberPerformItemDO item : performItemContext.getItems()) {
            MemberPerformItem itemPO = PerformCustomConvertor.toMemberPerformItem(item,
                    performItemContext);
            items.add(itemPO);
        }

        return items;
    }

    @Override
    public MemberPerformItem toMemberPerformItemWhenPerformSuccess(SubOrderPerformContext context, MemberPerformItemDO item) {
        return null;
    }
}