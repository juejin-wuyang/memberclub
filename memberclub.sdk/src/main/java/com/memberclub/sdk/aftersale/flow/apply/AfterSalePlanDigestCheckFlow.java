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
import com.memberclub.domain.context.aftersale.contant.AftersaleUnableCode;
import com.memberclub.infrastructure.dynamic_config.SwitchEnum;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class AfterSalePlanDigestCheckFlow extends FlowNode<AfterSaleApplyContext> {

    @Override
    public void process(AfterSaleApplyContext context) {
        if (SwitchEnum.AFTERSALE_PLAN_GENERATE_DIGEST_CHECK_DEGRADE.getBoolean(context.getCmd().getBizType().getCode())) {
            CommonLog.info("售后摘要降级,跳过校验 preview:{}, apply:{}",
                    context.getPreviewContext().getDigests(),
                    context.getCmd().getDigests());
            return;
        }
        if (context.getCmd().getSource() == AftersaleSourceEnum.System_Expire) {
            CommonLog.info("售后过期退，售后摘要跳过校验 preview:{}, apply:{}",
                    context.getPreviewContext().getDigests(),
                    context.getCmd().getDigests());
            return;
        }


        if (!StringUtils.equals(context.getPreviewContext().getDigests(), context.getCmd().getDigests())) {
            CommonLog.error("售后摘要发生变化,不能发起售后 preview:{}, apply:{}",
                    context.getPreviewContext().getDigests(),
                    context.getCmd().getDigests());
            throw AftersaleUnableCode.CONDITION_OCCUR.newException();
        } else {
            CommonLog.info("售后摘要保持一致,可以发起售后 preview:{}, apply:{}",
                    context.getPreviewContext().getDigests(),
                    context.getCmd().getDigests());

        }
    }
}