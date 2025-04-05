/**
 * @(#)MemberOrderDataObjectBuildFactory.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.memberorder;

import com.memberclub.common.util.JsonUtils;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.OrderSystemTypeEnum;
import com.memberclub.domain.context.perform.common.MemberOrderPerformStatusEnum;
import com.memberclub.domain.context.perform.common.SubOrderPerformStatusEnum;
import com.memberclub.domain.context.purchase.common.MemberOrderStatusEnum;
import com.memberclub.domain.context.purchase.common.SubOrderStatusEnum;
import com.memberclub.domain.context.purchase.common.SubmitSourceEnum;
import com.memberclub.domain.dataobject.order.MemberOrderExtraInfo;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.dataobject.perform.his.SubOrderExtraInfo;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.dataobject.purchase.OrderInfoDO;
import com.memberclub.domain.entity.trade.MemberOrder;
import com.memberclub.domain.entity.trade.MemberSubOrder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 */
@Service
public class MemberOrderDataObjectBuildFactory {

    public List<MemberOrderDO> buildMemberOrderDOS(List<MemberOrder> orders) {
        return orders.stream().map(order -> buildMemberOrderDO(order)).collect(Collectors.toList());
    }

    public MemberOrderDO buildMemberOrderDO(MemberOrder memberOrder) {
        MemberOrderDO orderDo = new MemberOrderDO();
        orderDo.setActPriceFen(memberOrder.getActPriceFen());
        orderDo.setBizType(BizTypeEnum.findByCode(memberOrder.getBizType()));
        orderDo.setCtime(memberOrder.getCtime());
        orderDo.setEtime(memberOrder.getEtime());
        orderDo.setExtra(JsonUtils.fromJson(memberOrder.getExtra(), MemberOrderExtraInfo.class));
        orderDo.setLocationInfo(orderDo.getExtra().getLocationInfo());
        orderDo.setOrderInfo(new OrderInfoDO());
        orderDo.getOrderInfo().setOrderId(memberOrder.getOrderId());
        orderDo.getOrderInfo().setOrderSystemType(OrderSystemTypeEnum.findByCode(memberOrder.getOrderSystemType()));
        orderDo.setOriginPriceFen(memberOrder.getOriginPriceFen());
        orderDo.setPerformStatus(MemberOrderPerformStatusEnum.findByCode(memberOrder.getPerformStatus()));
        orderDo.setSaleInfo(orderDo.getExtra().getSaleInfo());
        orderDo.setSalePriceFen(memberOrder.getSalePriceFen());
        orderDo.setSettleInfo(orderDo.getExtra().getSettleInfo());
        orderDo.setStatus(MemberOrderStatusEnum.findByCode(memberOrder.getStatus()));
        orderDo.setStime(memberOrder.getStime());
        orderDo.setTradeId(memberOrder.getTradeId());
        orderDo.setUserId(memberOrder.getUserId());
        orderDo.setSource(SubmitSourceEnum.findByCode(memberOrder.getSource()));
        orderDo.setUserInfo(orderDo.getExtra().getUserInfo());
        orderDo.setUtime(memberOrder.getUtime());
        return orderDo;
    }

    public MemberOrderDO buildMemberOrderDO(MemberOrder memberOrder, List<MemberSubOrder> subOrders) {
        MemberOrderDO orderDO = buildMemberOrderDO(memberOrder);
        orderDO.setSubOrders(subOrders.stream().map(this::buildMemberSubOrderDO).collect(Collectors.toList()));
        return orderDO;
    }

    public MemberSubOrderDO buildMemberSubOrderDO(MemberSubOrder memberSubOrder) {
        MemberSubOrderDO subOrder = new MemberSubOrderDO();
        subOrder.setActPriceFen(memberSubOrder.getActPriceFen());
        subOrder.setBizType(BizTypeEnum.findByCode(memberSubOrder.getBizType()));
        subOrder.setBuyCount(memberSubOrder.getBuyCount());
        subOrder.setCtime(memberSubOrder.getCtime());
        subOrder.setEtime(memberSubOrder.getEtime());
        subOrder.setExtra(JsonUtils.fromJson(memberSubOrder.getExtra(), SubOrderExtraInfo.class));
        subOrder.setPerformConfig(subOrder.getExtra().getPerformConfig());
        subOrder.setOrderId(memberSubOrder.getOrderId());
        subOrder.setOrderSystemType(OrderSystemTypeEnum.findByCode(memberSubOrder.getOrderSystemType()));
        subOrder.setOriginPriceFen(memberSubOrder.getOriginPriceFen());
        subOrder.setPerformStatus(SubOrderPerformStatusEnum.findByCode(memberSubOrder.getPerformStatus()));
        subOrder.setSalePriceFen(memberSubOrder.getSalePriceFen());
        subOrder.setSkuId(memberSubOrder.getSkuId());
        subOrder.setStatus(SubOrderStatusEnum.findByCode(memberSubOrder.getStatus()));
        subOrder.setStime(memberSubOrder.getStime());
        subOrder.setSubTradeId(memberSubOrder.getSubTradeId());
        subOrder.setTradeId(memberSubOrder.getTradeId());
        subOrder.setUserId(memberSubOrder.getUserId());
        subOrder.setUtime(memberSubOrder.getUtime());
        return subOrder;
    }
}