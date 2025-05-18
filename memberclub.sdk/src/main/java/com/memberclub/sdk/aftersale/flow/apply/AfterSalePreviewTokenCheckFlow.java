/**
 * @(#)AfterSalePlanDigestCheckFlow.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.flow.apply;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.aftersale.contant.AftersaleSourceEnum;
import com.memberclub.infrastructure.dynamic_config.SwitchEnum;
import com.memberclub.sdk.aftersale.service.domain.AfterSaleDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class AfterSalePreviewTokenCheckFlow extends FlowNode<AfterSaleApplyContext> {

    @Autowired
    private AfterSaleDomainService afterSaleDomainService;


    @Override
    public void process(AfterSaleApplyContext context) {
        if (SwitchEnum.AFTERSALE_PREVIEW_TOKEN_CHECK_DEGRADE.getBoolean(context.getApplyCmd().getBizType().getCode())) {
            CommonLog.info("售后预览降级,跳过校验 previewToken:{}", context.getExecuteCmd().getApplyCmd().getPreviewToken());
            return;
        }
        if (context.getApplyCmd().getSource() == AftersaleSourceEnum.System_Expire ||
                context.getApplyCmd().getSource() == AftersaleSourceEnum.SYSTEM_REFUND_4_PERFORM_FAIL ||
                context.getApplyCmd().getSource() == AftersaleSourceEnum.SYSTEM_REFUND_4_ORDER_PAY_TIMEOUT) {
            CommonLog.info("售后系统退款，售后预览结果跳过校验 previewToken:{}",
                    context.getExecuteCmd().getApplyCmd().getPreviewToken());
            return;
        }

        afterSaleDomainService.checkPreviewResult(context);
    }
}