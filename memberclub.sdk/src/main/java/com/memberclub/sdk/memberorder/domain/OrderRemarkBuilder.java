package com.memberclub.sdk.memberorder.domain;

import com.memberclub.common.util.ApplicationContextUtils;
import com.memberclub.domain.context.perform.common.MemberOrderPerformStatusEnum;
import com.memberclub.domain.context.purchase.common.MemberOrderStatusEnum;
import com.memberclub.domain.dataobject.payment.PayStatusEnum;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.entity.trade.OrderRemark;

import java.util.ArrayList;
import java.util.List;


public class OrderRemarkBuilder {

    private OrderRemark orderRemark = new OrderRemark();

    private List<String> remarkItems = new ArrayList<String>();

    private volatile OrderRemarkRepositoryService orderRemarkRepositoryService;

    public static OrderRemarkBuilder builder(int bizType, long userId, String tradeId) {
        OrderRemarkBuilder builder = new OrderRemarkBuilder();
        builder.orderRemark.setBizType(bizType);
        builder.orderRemark.setUserId(userId);
        builder.orderRemark.setTradeId(tradeId);
        return builder;
    }

    public static OrderRemarkBuilder builder(MemberOrderDO memberOrderDO) {
        return builder(memberOrderDO.getBizType().getCode(), memberOrderDO.getUserId(), memberOrderDO.getTradeId());
    }

    public OrderRemarkBuilder remark(MemberOrderStatusEnum newStatus, String tip) {
        remarkItems.add(String.format("新主状态:%s, tip:%s", newStatus.toString(), tip));
        return this;
    }

    public OrderRemarkBuilder remark(MemberOrderPerformStatusEnum newStatus, String tip) {
        remarkItems.add(String.format("新履约状态:%s, tip:%s", newStatus.toString(), tip));
        return this;
    }

    public OrderRemarkBuilder remark(PayStatusEnum newStatus, String tip) {
        remarkItems.add(String.format("新支付状态:%s, tip:%s", newStatus.toString(), tip));
        return this;
    }

    public OrderRemarkBuilder remark(MemberOrderStatusEnum newStatus, PayStatusEnum newPayStatus, String tip) {
        remarkItems.add(String.format("新主状态:%s，新支付状态:%s， tip:%s",
                newStatus.toString(), newPayStatus.toString(), tip));
        return this;
    }

    public OrderRemarkBuilder remark(String tip) {
        remarkItems.add(tip);
        return this;
    }

    public void save() {
        orderRemark.setDetail(String.join(";", remarkItems));
        if (orderRemarkRepositoryService == null) {
            synchronized (OrderRemarkBuilder.class) {
                if (orderRemarkRepositoryService == null) {
                    OrderRemarkRepositoryService remarkRepositoryService = ApplicationContextUtils.getContext().getBean(OrderRemarkRepositoryService.class);
                    this.orderRemarkRepositoryService = remarkRepositoryService;
                }
            }
        }
        orderRemarkRepositoryService.remark(this.orderRemark);
    }
}

