/**
 * @(#)PurchaseMarkNewMemberFlow.java, 一月 31, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.purchase.flow;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.dataobject.newmember.NewMemberMarkContext;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.dataobject.sku.SkuInfoDO;
import com.memberclub.sdk.newmember.service.NewMemberDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class PurchaseNewMemberSubmitFlow extends FlowNode<PurchaseSubmitContext> {

    @Autowired
    private NewMemberDomainService newMemberDomainService;

    @Override
    public void process(PurchaseSubmitContext context) {
        for (MemberSubOrderDO subOrder : context.getMemberOrder().getSubOrders()) {
            for (SkuInfoDO skuInfo : context.getSkuInfos()) {
                if (skuInfo.getSkuId() != subOrder.getSkuId()) {
                    continue;
                }
                NewMemberMarkContext markContext = new NewMemberMarkContext();
                markContext.setBizType(context.getBizType());
                markContext.setSkuNewMemberInfo(skuInfo.getExtra().getSkuNewMemberInfo());
                markContext.setSkuId(skuInfo.getSkuId());
                markContext.setUniqueKey(String.valueOf(subOrder.getSubTradeId()));
                markContext.setUserId(context.getUserId());
                newMemberDomainService.validate(markContext);

                subOrder.getExtra().setNewmember(markContext.isNewmember());
            }
        }
    }


    @Override
    public void success(PurchaseSubmitContext context) {
        for (MemberSubOrderDO subOrder : context.getMemberOrder().getSubOrders()) {
            for (SkuInfoDO skuInfo : context.getSkuInfos()) {
                if (skuInfo.getSkuId() != subOrder.getSkuId()) {
                    continue;
                }
                NewMemberMarkContext markContext = new NewMemberMarkContext();
                markContext.setBizType(context.getBizType());
                markContext.setSkuNewMemberInfo(skuInfo.getExtra().getSkuNewMemberInfo());
                markContext.setSkuId(skuInfo.getSkuId());
                markContext.setUniqueKey(String.valueOf(subOrder.getSubTradeId()));
                markContext.setUserId(context.getUserId());
                newMemberDomainService.mark(markContext);
            }
        }
    }
}