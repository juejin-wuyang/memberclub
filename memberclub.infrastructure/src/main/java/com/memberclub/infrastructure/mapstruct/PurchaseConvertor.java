/**
 * @(#)PurchaseConvertor.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.mapstruct;

import com.memberclub.domain.dataobject.outer.OuterSubmitRecordDO;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.dataobject.perform.his.SubOrderFinanceInfo;
import com.memberclub.domain.dataobject.perform.his.SubOrderSaleInfo;
import com.memberclub.domain.dataobject.perform.his.SubOrderViewInfo;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.dataobject.sku.SkuFinanceInfo;
import com.memberclub.domain.dataobject.sku.SkuSaleInfo;
import com.memberclub.domain.dataobject.sku.SkuViewInfo;
import com.memberclub.domain.entity.trade.MemberOrder;
import com.memberclub.domain.entity.trade.MemberSubOrder;
import com.memberclub.domain.entity.trade.OuterSubmitRecord;
import com.memberclub.infrastructure.mapstruct.custom.CommonCustomConvertor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * author: 掘金五阳
 */
@Mapper(uses = {CommonCustomConvertor.class})
public interface PurchaseConvertor {

    PurchaseConvertor INSTANCE = Mappers.getMapper(PurchaseConvertor.class);

    public SubOrderViewInfo toSubOrderViewInfo(SkuViewInfo viewInfo);

    public SubOrderFinanceInfo toSubOrderSettleInfo(SkuFinanceInfo settleInfo);

    public SubOrderSaleInfo toSubOrderSaleInfo(SkuSaleInfo saleInfo);


    @Mappings(value = {
            @Mapping(qualifiedByName = "toBizTypeInt", target = "bizType"),
            @Mapping(qualifiedByName = "toOrderSystemTypeInt", target = "orderSystemType"),
            @Mapping(qualifiedByName = "toMemberSubOrderExtraString", target = "extra"),
            @Mapping(qualifiedByName = "toSubOrderStatusEnumInt", target = "status"),
            @Mapping(qualifiedByName = "toSubOrderPerformStatusEnumInt", target = "performStatus"),
    })
    public MemberSubOrder toMemberSubOrder(MemberSubOrderDO subOrder);


    @Mappings(value = {
            @Mapping(qualifiedByName = "toBizTypeInt", target = "bizType"),
            @Mapping(qualifiedByName = "toOrderSystemTypeInt", source = "orderInfo.orderSystemType", target = "orderSystemType"),
            @Mapping(source = "orderInfo.orderId", target = "orderId"),
            @Mapping(source = "saleInfo.renewType", target = "renewType", qualifiedByName = "toRenewTypeEnumInt"),

            @Mapping(target = "extra", source = "extra", qualifiedByName = "toMemberOrderExtraInfoString"),
            @Mapping(target = "status", qualifiedByName = "toMemberOrderStatusEnumInt"),
            @Mapping(target = "performStatus", qualifiedByName = "toMemberOrderPerformStatusEnumInt"),
            @Mapping(target = "source", qualifiedByName = "toPurchaseSourceEnumInt"),
    })
    public MemberOrder toMemberOrder(MemberOrderDO order);


    @Mappings(value = {
            @Mapping(source = "bizType", target = "bizType", ignore = true),
            @Mapping(source = "extra", target = "extra", ignore = true),
            @Mapping(source = "outerType", target = "outerType", ignore = true),
            @Mapping(source = "status", target = "status", ignore = true),
    })
    public OuterSubmitRecord toOuterSubmitRecord(OuterSubmitRecordDO record);

}