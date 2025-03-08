/**
 * @(#)PerformConvertor.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.mapstruct;

import com.memberclub.domain.context.perform.PerformCmd;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.PerformItemContext;
import com.memberclub.domain.context.perform.period.PeriodPerformContext;
import com.memberclub.domain.dataobject.membership.MemberShipDO;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.domain.dataobject.perform.item.PerformItemFinanceInfo;
import com.memberclub.domain.dataobject.perform.item.PerformItemGrantInfo;
import com.memberclub.domain.dataobject.perform.item.PerformItemSaleInfo;
import com.memberclub.domain.dataobject.perform.item.PerformItemViewInfo;
import com.memberclub.domain.dataobject.sku.SkuInfoDO;
import com.memberclub.domain.dataobject.sku.SkuPerformItemConfigDO;
import com.memberclub.domain.dataobject.sku.rights.RightFinanceInfo;
import com.memberclub.domain.dataobject.sku.rights.RightGrantInfo;
import com.memberclub.domain.dataobject.sku.rights.RightSaleInfo;
import com.memberclub.domain.dataobject.sku.rights.RightViewInfo;
import com.memberclub.domain.dataobject.task.OnceTaskDO;
import com.memberclub.domain.dataobject.task.perform.PerformTaskContentItemDO;
import com.memberclub.domain.dto.sku.MemberSkuDTO;
import com.memberclub.domain.entity.trade.MemberPerformItem;
import com.memberclub.domain.entity.trade.MemberShip;
import com.memberclub.domain.entity.trade.OnceTask;
import com.memberclub.infrastructure.mapstruct.custom.CommonCustomConvertor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author 掘金五阳
 */
@Mapper(uses = {PerformCustomConvertor.class, CommonCustomConvertor.class})
public interface PerformConvertor {

    PerformConvertor INSTANCE = Mappers.getMapper(PerformConvertor.class);

    @Mappings({
            @Mapping(target = "bizType", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "extra", ignore = true),
            @Mapping(target = "shipType", ignore = true),

    })
    public MemberShip toMemberShip(MemberShipDO memberShipDO);

    @Mappings({
            @Mapping(target = "bizType", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "extra", ignore = true),
            @Mapping(target = "shipType", ignore = true),

    })
    public MemberShipDO toMemberShipDO(MemberShip memberShipDO);

    @Deprecated
    public SkuInfoDO toMemberSkuInfoDO(MemberSkuDTO dto);

    public PerformContext toPerformContext(PerformCmd cmd);

    @Mappings(value = {
            @Mapping(qualifiedByName = "toRightTypeEnum", target = "rightType"),
            @Mapping(target = "periodType", source = "periodType", qualifiedByName = "toPeriodTypeEnum"),
            @Mapping(target = "extra.grantInfo", source = "grantInfo"),
            @Mapping(target = "extra.viewInfo", source = "viewInfo"),
            @Mapping(target = "extra.saleInfo", source = "saleInfo"),
            @Mapping(target = "extra.settleInfo", source = "settleInfo"),
            @Mapping(target = "extra.grantInfo.periodCount", source = "periodCount"),
            @Mapping(target = "extra.grantInfo.periodType", source = "periodType"),

    })
    public MemberPerformItemDO toPerformItem(SkuPerformItemConfigDO performConfigDO);

    @Mappings({
            @Mapping(qualifiedByName = "toRightTypeEnum", target = "rightType"),
            @Mapping(qualifiedByName = "toGrantTypeEnum", target = "grantType"),
            @Mapping(qualifiedByName = "toPerformItemExtraInfo", target = "extra"),
            @Mapping(qualifiedByName = "toPeriodTypeEnum", target = "periodType"),
            @Mapping(qualifiedByName = "toPerformItemStatusEnum", target = "status"),

    })
    public MemberPerformItemDO toMemberPerformItemDOForPeriodPerform(PerformTaskContentItemDO item);

    @Mappings(value = {
            @Mapping(qualifiedByName = "toRightTypeInt", target = "rightType"),
            @Mapping(qualifiedByName = "toGrantTypeInt", target = "grantType"),
            @Mapping(qualifiedByName = "toExtraInfoString", target = "extra"),
            @Mapping(qualifiedByName = "toPerformItemStatusEnumInt", target = "status"),
    })
    public MemberPerformItem toMemberPerformItem(MemberPerformItemDO item);

    public PerformItemContext toPerformItemContextForPeriodPerform(PeriodPerformContext context);


    public PeriodPerformContext toPeriodPerformContextForTask(OnceTaskDO task);

    @Mappings({
            @Mapping(target = "bizType", qualifiedByName = "toBizTypeEnum"),
            @Mapping(target = "taskType", qualifiedByName = "toTaskTypeEnum"),
            @Mapping(target = "status", qualifiedByName = "toTaskStatusEnum"),
            @Mapping(target = "content", ignore = true)
    })
    public OnceTaskDO toOnceTaskDO(OnceTask onceTask);

    @Mappings(value = {
            @Mapping(qualifiedByName = "toRightTypeInt", target = "rightType"),
            @Mapping(qualifiedByName = "toGrantTypeInt", target = "grantType"),
            @Mapping(qualifiedByName = "toExtraInfoString", target = "extra"),
            @Mapping(qualifiedByName = "toPerformItemStatusEnumInt", target = "status"),
    })
    public PerformTaskContentItemDO toPerformTaskContentItemDO(MemberPerformItemDO item);


    public MemberPerformItemDO copyPerformItem(MemberPerformItemDO memberPerformItemDO);

    @Mappings(value = {
            @Mapping(qualifiedByName = "toBizTypeInt", target = "bizType"),
            @Mapping(qualifiedByName = "toTaskStatusInt", target = "status"),
            @Mapping(qualifiedByName = "toTaskTypeInt", target = "taskType"),
            @Mapping(qualifiedByName = "toTaskContentString", target = "content"),
    })
    public OnceTask toOnceTask(OnceTaskDO task);


    public PerformItemViewInfo toViewInfo(RightViewInfo viewInfo);

    public PerformItemFinanceInfo toSettleInfo(RightFinanceInfo settleInfo);

    public PerformItemSaleInfo toSaleInfo(RightSaleInfo saleInfo);

    public PerformItemGrantInfo toGrantInfo(RightGrantInfo grantInfo);
}