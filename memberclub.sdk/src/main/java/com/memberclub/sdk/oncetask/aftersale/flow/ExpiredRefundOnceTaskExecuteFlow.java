/**
 * @(#)OnceTaskExecuteOnExpireRefundFlow.java, 一月 27, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.oncetask.aftersale.flow;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.context.aftersale.apply.AftersaleApplyCmd;
import com.memberclub.domain.context.aftersale.apply.AftersaleApplyResponse;
import com.memberclub.domain.context.aftersale.contant.AftersaleSourceEnum;
import com.memberclub.domain.context.oncetask.execute.OnceTaskExecuteContext;
import com.memberclub.sdk.aftersale.service.AftersaleBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class ExpiredRefundOnceTaskExecuteFlow extends FlowNode<OnceTaskExecuteContext> {

    @Autowired
    private AftersaleBizService aftersaleBizService;

    @Override
    public void process(OnceTaskExecuteContext context) {
        AftersaleApplyCmd cmd = new AftersaleApplyCmd();
        cmd.setUserId(context.getOnceTask().getUserId());
        cmd.setTradeId(context.getOnceTask().getTradeId());
        cmd.setSource(AftersaleSourceEnum.System_Expire);
        cmd.setOperator("system");
        cmd.setBizType(context.getOnceTask().getBizType());
        cmd.setReason("system expire refund");

        AftersaleApplyResponse response = aftersaleBizService.apply(cmd);
        CommonLog.warn("过期退结果:{}", response);
    }
}