/**
 * @(#)GenerateAftersalePlanDigestFlow.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.flow.preview;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.aftersale.preview.AftersalePreviewContext;
import com.memberclub.infrastructure.dynamic_config.SwitchEnum;
import com.memberclub.sdk.aftersale.extension.preview.GenerateAfterSalePlanDigestExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 * 生成售后计划摘要
 */
@Service
public class AftersalePlanDigestGenerateFlow extends FlowNode<AftersalePreviewContext> {

    @Autowired
    private ExtensionManager extensionManager;

    @Override
    public void process(AftersalePreviewContext context) {
        if (context.getDigestVersion() == null) {
            int version = SwitchEnum.AFTERSALE_PLAN_GENERATE_DIGEST_VERSION.getInt(context.getCmd().getBizType().getCode());
            context.setDigestVersion(version);
        }

        extensionManager.getExtension(
                BizScene.of(context.getCmd().getBizType().getCode(), String.valueOf(context.getDigestVersion())),
                GenerateAfterSalePlanDigestExtension.class).generateDigest(context);
    }
}