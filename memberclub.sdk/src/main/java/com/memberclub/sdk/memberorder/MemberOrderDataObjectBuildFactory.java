/**
 * @(#)MemberOrderBuildFactory.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.memberorder;

import com.google.common.collect.Lists;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.util.JsonUtils;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.OrderSystemTypeEnum;
import com.memberclub.domain.context.perform.common.MemberOrderPerformStatusEnum;
import com.memberclub.domain.context.perform.common.SubOrderPerformStatusEnum;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.context.purchase.cancel.PurchaseCancelContext;
import com.memberclub.domain.context.purchase.common.MemberOrderStatusEnum;
import com.memberclub.domain.context.purchase.common.RenewTypeEnum;
import com.memberclub.domain.context.purchase.common.SubOrderStatusEnum;
import com.memberclub.domain.context.purchase.common.SubmitSourceEnum;
import com.memberclub.domain.dataobject.order.MemberOrderExtraInfo;
import com.memberclub.domain.dataobject.order.MemberOrderFinanceInfo;
import com.memberclub.domain.dataobject.order.MemberOrderSaleInfo;
import com.memberclub.domain.dataobject.payment.PayNodeTypeEnum;
import com.memberclub.domain.dataobject.payment.PayOnlineTypeEnum;
import com.memberclub.domain.dataobject.payment.PayStatusEnum;
import com.memberclub.domain.dataobject.payment.PaymentDO;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.dataobject.perform.his.SubOrderExtraInfo;
import com.memberclub.domain.dataobject.perform.his.SubOrderFinanceInfo;
import com.memberclub.domain.dataobject.perform.his.SubOrderSaleInfo;
import com.memberclub.domain.dataobject.perform.his.SubOrderViewInfo;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.dataobject.purchase.OrderInfoDO;
import com.memberclub.domain.dataobject.sku.SkuInfoDO;
import com.memberclub.domain.dataobject.sku.SkuPerformConfigDO;
import com.memberclub.domain.entity.trade.MemberOrder;
import com.memberclub.domain.entity.trade.MemberSubOrder;
import com.memberclub.infrastructure.id.IdTypeEnum;
import com.memberclub.infrastructure.mapstruct.PurchaseConvertor;
import com.memberclub.sdk.common.IdGeneratorDomainService;
import com.memberclub.sdk.purchase.extension.PurchaseOrderBuildExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 */
@Service
public class MemberOrderDataObjectBuildFactory {

    @Autowired
    private IdGeneratorDomainService idGeneratorDomainService;

    @Autowired
    private ExtensionManager extensionManager;

    public void onSubmitSuccess(PurchaseSubmitContext context) {
        context.getMemberOrder().onSubmitSuccess(context);
        extensionManager.getExtension(context.toDefaultBizScene(),
                PurchaseOrderBuildExtension.class).onSubmitSuccess(context.getMemberOrder(), context);


    }

    public void onSubmitFail(PurchaseSubmitContext context, Exception e) {
        context.getMemberOrder().onSubmitFail(context);
        extensionManager.getExtension(context.toDefaultBizScene(),
                PurchaseOrderBuildExtension.class).onSubmitFail(context.getMemberOrder(), context, e);
    }

    public void onSubmitCancel(PurchaseCancelContext context) {
        context.getMemberOrder().onSubmitCancel(context);
        extensionManager.getExtension(BizScene.of(context.getCmd().getBizType()),
                PurchaseOrderBuildExtension.class).onSubmitCancel(context.getMemberOrder(), context);
    }


    public MemberOrder buildOrder4Create(MemberOrderDO memberOrderDO) {
        MemberOrder order = PurchaseConvertor.INSTANCE.toMemberOrder(memberOrderDO);

        order.setPayStatus(PayStatusEnum.INIT.getCode());
        order.setPayAccount("");
        order.setPayTime(0);
        order.setPayChannelType("");
        order.setPayAmountFen(null);
        order.setPayNodeType("");
        order.setPayAccountType("");
        order.setPayTradeNo("");
        order.setPayOnlineType("");
        return order;
    }

    public MemberOrderDO build(PurchaseSubmitContext context) {
        MemberOrderDO order = new MemberOrderDO();
        order.setBizType(context.getBizType());
        order.setCtime(context.getStartTime());
        order.setUtime(order.getCtime());
        order.setLocationInfo(context.getSubmitCmd().getLocationInfo());
        order.setOrderInfo(new OrderInfoDO());
        order.setTradeId(idGeneratorDomainService.generateOrderId(context.getUserId()).toString());
        order.setUserId(context.getUserId());
        order.setSource(context.getSubmitCmd().getSource());
        order.setUserInfo(context.getUserInfo());
        order.setStatus(MemberOrderStatusEnum.INIT);
        order.setPerformStatus(com.memberclub.domain.context.perform.common.MemberOrderPerformStatusEnum.INIT);
        order.setSettleInfo(new MemberOrderFinanceInfo());
        order.setSaleInfo(new MemberOrderSaleInfo());
        order.getSaleInfo().setRenewType(RenewTypeEnum.NONE);
        order.setExtra(new MemberOrderExtraInfo());
        order.getExtra().setLocationInfo(order.getLocationInfo());
        order.getExtra().setUserInfo(order.getUserInfo());
        order.getExtra().setSettleInfo(order.getSettleInfo());
        order.getExtra().setSaleInfo(order.getSaleInfo());
        order.getExtra().setLockValue(context.getLockValue());
        order.getExtra().setStartTime(context.getStartTime());

        extensionManager.getExtension(context.toDefaultBizScene(),
                PurchaseOrderBuildExtension.class).buildOrder(order, context);

        List<MemberSubOrderDO> subOrders = Lists.newArrayList();
        order.setSubOrders(subOrders);
        for (SkuInfoDO skuInfo : context.getSkuInfos()) {
            MemberSubOrderDO subOrder = new MemberSubOrderDO();
            subOrder.setBizType(context.getBizType());
            subOrder.setBuyCount(skuInfo.getBuyCount());
            subOrder.setCtime(TimeUtil.now());
            subOrder.setExtra(new SubOrderExtraInfo());
            subOrder.setSkuId(skuInfo.getSkuId());
            subOrder.setTradeId(order.getTradeId());
            subOrder.setUserId(context.getUserId());
            subOrder.setUtime(TimeUtil.now());
            SubOrderViewInfo viewInfo = PurchaseConvertor.INSTANCE.toSubOrderViewInfo(skuInfo.getViewInfo());

            SubOrderFinanceInfo settleInfo = PurchaseConvertor.INSTANCE.toSubOrderSettleInfo(skuInfo.getFinanceInfo());

            SubOrderSaleInfo saleInfo = PurchaseConvertor.INSTANCE.toSubOrderSaleInfo(skuInfo.getSaleInfo());

            SkuPerformConfigDO performConfig = skuInfo.getPerformConfig();
            subOrder.getExtra().setPerformConfig(performConfig);
            subOrder.getExtra().setSettleInfo(settleInfo);
            subOrder.getExtra().setViewInfo(viewInfo);
            subOrder.getExtra().setUserInfo(context.getUserInfo());
            subOrder.getExtra().setSaleInfo(saleInfo);
            subOrder.getExtra().setSkuInventoryInfo(skuInfo.getInventoryInfo());
            subOrder.getExtra().setSkuRestrictInfo(skuInfo.getRestrictInfo());
            subOrder.getExtra().setSkuNewMemberInfo(skuInfo.getExtra().getSkuNewMemberInfo());

            subOrder.setStatus(SubOrderStatusEnum.INIT);
            subOrder.setPerformStatus(SubOrderPerformStatusEnum.INIT);
            subOrder.setSubTradeId(idGeneratorDomainService.generateId(IdTypeEnum.SUB_ORDER_ID));
            subOrder.setOriginPriceFen(skuInfo.getSaleInfo().getOriginPriceFen() * skuInfo.getBuyCount());
            subOrder.setSalePriceFen(skuInfo.getSaleInfo().getSalePriceFen() * skuInfo.getBuyCount());
            subOrder.setActPriceFen(subOrder.getActPriceFen());

            extensionManager.getExtension(context.toDefaultBizScene(),
                    PurchaseOrderBuildExtension.class).buildSubOrder(order, subOrder, context, skuInfo);

            subOrders.add(subOrder);
        }

        order.setOriginPriceFen(subOrders.stream().collect(Collectors.summingInt(MemberSubOrderDO::getOriginPriceFen)));
        order.setSalePriceFen(subOrders.stream().collect(Collectors.summingInt(MemberSubOrderDO::getSalePriceFen)));
        return order;
    }


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
        orderDo.getOrderInfo().setRelatedOrderId(memberOrder.getRelatedOrderId());
        orderDo.getOrderInfo().setRelatedOrderSystemType(OrderSystemTypeEnum.findByCode(memberOrder.getRelatedOrderSystemType()));
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

        PaymentDO paymentDO = new PaymentDO();
        paymentDO.setPayNodeType(PayNodeTypeEnum.find(memberOrder.getPayNodeType()));
        paymentDO.setPayStatus(PayStatusEnum.findByCode(memberOrder.getPayStatus()));
        paymentDO.setMerchantId(memberOrder.getMerchantId());
        paymentDO.setPayOnlineType(PayOnlineTypeEnum.find(memberOrder.getPayOnlineType()));
        paymentDO.setPayAccount(memberOrder.getPayAccount());
        paymentDO.setPayTime(memberOrder.getPayTime());
        paymentDO.setPayAmountFen(memberOrder.getPayAmountFen());
        paymentDO.setPayAccountType(memberOrder.getPayAccountType());
        paymentDO.setPayChannelType(memberOrder.getPayChannelType());

        orderDo.setPaymentInfo(paymentDO);

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
        subOrder.setRelatedOrderId(memberSubOrder.getRelatedOrderId());
        subOrder.setRelatedOrderSystemType(OrderSystemTypeEnum.findByCode(memberSubOrder.getRelatedOrderSystemType()));
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