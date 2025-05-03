/**
 * @(#)MemberOrderDomainExtension.java, 一月 08, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.memberorder.extension;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.entity.trade.MemberOrder;
import org.springframework.transaction.annotation.Transactional;

/**
 * author: 掘金五阳
 */
@ExtensionConfig(desc = "MemberOrder Domain 层扩展点", type = ExtensionType.PURCHASE, must = true)
public interface MemberOrderDomainExtension extends BaseExtension {

    public void onSubmitSuccess(MemberOrderDO memberOrderDO, LambdaUpdateWrapper<MemberOrder> wrapper);

    public void onSubmitCancel(MemberOrderDO memberOrderDO, LambdaUpdateWrapper<MemberOrder> wrapper);

    public int onStartPerform(PerformContext context, LambdaUpdateWrapper<MemberOrder> wrapper);

    public void onPerformSuccess(PerformContext context, MemberOrderDO memberOrderDO, LambdaUpdateWrapper<MemberOrder> wrapper);

    public void onReversePerformSuccess(ReversePerformContext context, MemberOrderDO memberOrderDO, LambdaUpdateWrapper<MemberOrder> wrapper);

    public void onPrePay(MemberOrderDO memberOrderDO, LambdaUpdateWrapper<MemberOrder> wrapper);

    @Transactional
    void onPaySuccess(MemberOrderDO memberOrderDO, LambdaUpdateWrapper<MemberOrder> wrapper);

    @Transactional
    void onPaySuccess4OrderTimeout(MemberOrderDO memberOrderDO, LambdaUpdateWrapper<MemberOrder> wrapper);

    @Transactional
    void onRefund4OrderTimeout(MemberOrderDO memberOrderDO, LambdaUpdateWrapper<MemberOrder> wrapper);
}