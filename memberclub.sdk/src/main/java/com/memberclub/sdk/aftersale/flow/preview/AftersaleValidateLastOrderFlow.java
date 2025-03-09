package com.memberclub.sdk.aftersale.flow.preview;

import cn.hutool.core.collection.CollectionUtil;
import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.aftersale.contant.AftersaleUnableCode;
import com.memberclub.domain.context.aftersale.preview.AftersalePreviewContext;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.sdk.purchase.service.RenewDomainService;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class AftersaleValidateLastOrderFlow extends FlowNode<AftersalePreviewContext> {

    @Autowired
    private RenewDomainService renewDomainService;

    @Override
    public void process(AftersalePreviewContext context) {
        List<MemberOrderDO> orders = renewDomainService.getNonExpiredMemberOrders(context.getCmd().getUserId(),
                context.getCmd().getBizType().getCode());
        if (CollectionUtil.isEmpty(orders)) {
            return;
        }
        orders = CollectionUtil.sort(orders, Comparator.comparingLong(MemberOrderDO::getCtime).reversed());
        if (!StringUtil.equals(orders.get(0).getTradeId(), context.getCmd().getTradeId())) {
            throw AftersaleUnableCode.REFUND_NONLAST_ORDER_ERROR.newException();
        }
    }
}
