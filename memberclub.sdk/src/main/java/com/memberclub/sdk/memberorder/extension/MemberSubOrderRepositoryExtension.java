/**
 * @(#)MemberSubOrderDomainExtension.java, 一月 08, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.memberorder.extension;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.SubOrderPerformContext;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.domain.context.perform.reverse.SubOrderReversePerformContext;
import com.memberclub.domain.dataobject.payment.context.PaymentNotifyContext;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.entity.trade.MemberSubOrder;

/**
 * author: 掘金五阳
 */
@ExtensionConfig(desc = "MemberSubOrder domain 层数据修改扩展点", type = ExtensionType.PURCHASE, must = true)
public interface MemberSubOrderRepositoryExtension extends BaseExtension {

    public void onSubmitSuccess(MemberSubOrderDO memberSubOrderDO, LambdaUpdateWrapper<MemberSubOrder> wrapper);


    public void onSubmitCancel(MemberSubOrderDO memberSubOrderDO, LambdaUpdateWrapper<MemberSubOrder> wrapper);


    /**
     * 开始履约 会员子单
     *
     * @param performContext
     * @param subOrderPerformContext
     * @param memberSubOrderDO
     * @param wrapper
     */
    public void onStartPerform(PerformContext performContext,
                               SubOrderPerformContext subOrderPerformContext,
                               MemberSubOrderDO memberSubOrderDO,
                               LambdaUpdateWrapper<MemberSubOrder> wrapper);

    public void onPerformSuccess(PerformContext performContext,
                                 SubOrderPerformContext subOrderPerformContext,
                                 MemberSubOrderDO memberSubOrderDO,
                                 LambdaUpdateWrapper<MemberSubOrder> wrapper);

    public void onPaymentSuccess(PaymentNotifyContext context, MemberOrderDO order, MemberSubOrderDO subOrder, LambdaUpdateWrapper<MemberSubOrder> wrapper);

    public void onStartReversePerform(ReversePerformContext context,
                                      SubOrderReversePerformContext subOrderReversePerformContext,
                                      MemberSubOrderDO subOrder,
                                      LambdaUpdateWrapper<MemberSubOrder> wrapper);

    public void onReversePerformSuccess(ReversePerformContext context,
                                        SubOrderReversePerformContext subOrderReversePerformContext,
                                        MemberSubOrderDO subOrder,
                                        LambdaUpdateWrapper<MemberSubOrder> wrapper);

    public void onRefundSuccess(AfterSaleApplyContext context,
                                MemberSubOrderDO subOrder,
                                LambdaUpdateWrapper<MemberSubOrder> wrapper);


}