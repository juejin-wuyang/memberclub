/**
 * @(#)AftersalePreviewDegradeFlow.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.flow.preview;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.context.aftersale.contant.AftersaleUnableCode;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewContext;
import com.memberclub.sdk.common.DegradeSwitchService;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class AftersalePreviewDegradeFlow extends FlowNode<AfterSalePreviewContext> {

    @Override
    public void process(AfterSalePreviewContext context) {
        boolean degrade = DegradeSwitchService.degrade4AfterSale(context);
        if (degrade) {
            CommonLog.warn("渠道{} 已经降级,不能发起售后", context.getCmd().getSource().toString());
            throw AftersaleUnableCode.DEGRADE_AFTERSALE_ERROR.newException(null, null);
        }
    }
}