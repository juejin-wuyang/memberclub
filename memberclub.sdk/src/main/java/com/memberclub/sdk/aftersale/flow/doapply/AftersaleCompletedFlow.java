/**
 * @(#)AftersaleAsyncRollbackFlow.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.flow.doapply;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.sdk.aftersale.service.domain.AfterSaleDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 * 如果流程失败,可以发起异步回滚流程.
 * 可选流程.
 */
@Service
public class AftersaleCompletedFlow extends FlowNode<AfterSaleApplyContext> {

    @Autowired
    private AfterSaleDomainService afterSaleDomainService;

    @Override
    public void process(AfterSaleApplyContext context) {

    }

    @Override
    public void success(AfterSaleApplyContext context) {
        afterSaleDomainService.onAftersaleSuccess(context, context.getAftersaleOrderDO());
    }

    @Override
    public void rollback(AfterSaleApplyContext context, Exception e) {
        // TODO: 2025/1/5 确定是否是最后一次重试,那么就开启回滚


    }
}