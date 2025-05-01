/**
 * @(#)PurchaseCancelNewMemberFlow.java, 二月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.purchase.flow.cancel;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.purchase.cancel.PurchaseCancelContext;
import com.memberclub.domain.dataobject.newmember.NewMemberMarkContext;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.sdk.newmember.service.NewMemberDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class PurchaseNewMemberCancelFlow extends FlowNode<PurchaseCancelContext> {

    @Autowired
    private NewMemberDomainService newMemberDomainService;

    @Override
    public void process(PurchaseCancelContext context) {
        MemberOrderDO memberOrderDO = context.getMemberOrder();
        for (MemberSubOrderDO subOrder : memberOrderDO.getSubOrders()) {
            NewMemberMarkContext markContext = new NewMemberMarkContext();

            markContext.setBizType(memberOrderDO.getBizType());
            markContext.setSkuId(subOrder.getSkuId());
            markContext.setUniqueKey(String.valueOf(subOrder.getSubTradeId()));
            markContext.setUserId(memberOrderDO.getUserId());
            markContext.setSkuNewMemberInfo(subOrder.getExtra().getSkuNewMemberInfo());
            newMemberDomainService.unmark(markContext);
        }
    }
}