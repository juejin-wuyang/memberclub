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
import com.memberclub.domain.context.aftersale.preview.AftersalePreviewContext;
import com.memberclub.infrastructure.dynamic_config.SwitchEnum;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class AftersalePreviewDegradeFlow extends FlowNode<AftersalePreviewContext> {


    @Override
    public void process(AftersalePreviewContext context) {
        boolean degrade = SwitchEnum.AFTERSALE_DEGRADE.getBoolean(context.getCmd().getBizType().getCode(),
                String.valueOf(context.getCmd().getSource().getCode()));
        if (degrade) {
            CommonLog.warn("渠道{} 已经降级,不能发起售后", context.getCmd().getSource().toString());
            throw AftersaleUnableCode.DEGRADE_AFTERSALE_ERROR.newException(null, null);
        }
    }
}