/**
 * @(#)AftersaleApplyLockFlow.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.flow.apply;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.sdk.perform.service.domain.MemberTradeLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class AfterSaleResourceLockFlow extends FlowNode<AfterSaleApplyContext> {

    @Autowired
    private MemberTradeLockService memberTradeLockService;

    @Override
    public void process(AfterSaleApplyContext context) {
        //加锁失败,抛出异常
        memberTradeLockService.lockOnPreAfterSale(context);
    }

    @Override
    public void callback(AfterSaleApplyContext context, Exception e) {
        if (e != null) {
            memberTradeLockService.unlockOnAfterSaleFail(context, e);
        } else {
            // TODO: 2025/1/1 释放锁
            memberTradeLockService.unlockOnAfterSaleSuccess(context);
        }
    }
}