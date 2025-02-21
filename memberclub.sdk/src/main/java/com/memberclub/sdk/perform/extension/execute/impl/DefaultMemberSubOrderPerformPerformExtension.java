/**
 * @(#)DefaultMemberPerformHisExtension.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.extension.execute.impl;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.SubOrderPerformContext;
import com.memberclub.domain.context.perform.common.SubOrderPerformStatusEnum;
import com.memberclub.domain.context.purchase.common.SubOrderStatusEnum;
import com.memberclub.sdk.perform.extension.execute.MemberSubOrderPerformExtension;
import com.memberclub.sdk.perform.service.domain.PerformDomainService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "默认履约上下文构建", bizScenes = {@Route(bizType = BizTypeEnum.DEFAULT, scenes = SceneEnum.DEFAULT_SCENE)})
public class DefaultMemberSubOrderPerformPerformExtension implements MemberSubOrderPerformExtension {

    @Autowired
    private ExtensionManager extensionManager;

    @Autowired
    private PerformDomainService performDomainService;

    @Override
    public void buildMemberSubOrderOnStartPerform(PerformContext context, SubOrderPerformContext subOrderPerformContext) {

    }

    @Override
    public void buildMemberSubOrderWhenPerformSuccess(PerformContext context, SubOrderPerformContext subOrderPerformContext) {
        subOrderPerformContext.getSubOrder().setStatus(SubOrderStatusEnum.PERFORMED);
        subOrderPerformContext.getSubOrder().setPerformStatus(SubOrderPerformStatusEnum.PERFORM_SUCCESS);
        subOrderPerformContext.getSubOrder().setUtime(TimeUtil.now());
        return;
    }

    @Override
    public void buildCommonExtraInfoOnPrePerform(PerformContext context, SubOrderPerformContext subOrderPerformContext) {
        performDomainService.buildSubOrderExtraInfo(context, subOrderPerformContext);
    }


}