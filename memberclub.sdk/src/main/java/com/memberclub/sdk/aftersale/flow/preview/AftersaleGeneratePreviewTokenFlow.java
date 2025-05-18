/**
 * @(#)GenerateAftersalePlanDigestFlow.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.flow.preview;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.flow.FlowNode;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewContext;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewCoreResult;
import com.memberclub.sdk.aftersale.extension.preview.AfterSalePreviewCheckExtension;
import com.memberclub.sdk.aftersale.service.domain.AfterSaleDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 * 生成售后计划摘要
 */
@Service
public class AftersaleGeneratePreviewTokenFlow extends FlowNode<AfterSalePreviewContext> {

    @Autowired
    private ExtensionManager extensionManager;

    @Autowired
    private AfterSaleDomainService afterSaleDomainService;

    @Override
    public void process(AfterSalePreviewContext context) {
        if (context.isPreviewBeforeApply()) {
            CommonLog.info("售后受理阶段不生成预览token");
            return;
        }

        AfterSalePreviewCheckExtension extension = extensionManager.getExtension(
                BizScene.of(context.getCmd().getBizType().getCode(), context.getCmd().getSource().getCode() + ""), AfterSalePreviewCheckExtension.class);
        AfterSalePreviewCoreResult result = extension.generatePreviewCoreResult(context);

        String previewToken = afterSaleDomainService.generatePreviewToken(context.getCmd().getSource(), context.getCmd().getTradeId());
        context.setPreviewToken(previewToken);

        afterSaleDomainService.savePreviewToken(context, previewToken, result);
    }
}