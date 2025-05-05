/**
 * @(#)PerformCustomConvertor.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.mapstruct.custom;

import com.memberclub.common.util.JsonUtils;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.OrderSystemTypeEnum;
import com.memberclub.domain.context.aftersale.contant.RefundWayEnum;
import com.memberclub.domain.context.oncetask.common.OnceTaskStatusEnum;
import com.memberclub.domain.context.oncetask.common.TaskTypeEnum;
import com.memberclub.domain.context.perform.common.SubOrderPerformStatusEnum;
import com.memberclub.domain.context.purchase.common.MemberOrderStatusEnum;
import com.memberclub.domain.context.purchase.common.RenewTypeEnum;
import com.memberclub.domain.context.purchase.common.SubOrderStatusEnum;
import com.memberclub.domain.context.purchase.common.SubmitSourceEnum;
import com.memberclub.domain.dataobject.order.MemberOrderExtraInfo;
import com.memberclub.domain.dataobject.perform.his.SubOrderExtraInfo;
import com.memberclub.domain.dataobject.task.TaskContentDO;
import org.mapstruct.Named;

/**
 * author: 掘金五阳
 */
public class CommonCustomConvertor {
    @Named("toBizTypeInt")
    public int toBizTypeInt(BizTypeEnum bizType) {
        return bizType.getCode();
    }

    @Named("toBizTypeEnum")
    public BizTypeEnum toBizType(int bizType) {
        return BizTypeEnum.findByCode(bizType);
    }


    @Named("toOrderSystemTypeInt")
    public Integer toOrderSystemTypeInt(OrderSystemTypeEnum orderSystemType) {
        if (orderSystemType == null) {
            return null;
        }
        return orderSystemType.getCode();
    }

    @Named("toSubOrderPerformStatusEnumInt")
    public int toSubOrderPerformStatusEnumInt(SubOrderPerformStatusEnum e) {
        return e.getCode();
    }

    @Named("toSubOrderPerformStatusEnum")
    public SubOrderPerformStatusEnum toSubOrderPerformStatusEnumEnum(int code) {
        return SubOrderPerformStatusEnum.findByCode(code);
    }

    @Named("toSubOrderStatusEnumInt")
    public int toSubOrderStatusEnumInt(SubOrderStatusEnum e) {
        return e.getCode();
    }

    @Named("toSubOrderStatusEnum")
    public SubOrderStatusEnum toSubOrderStatusEnumEnum(int code) {
        return SubOrderStatusEnum.findByCode(code);
    }


    @Named("toMemberSubOrderExtraString")
    public String toMemberSubOrderExtraString(SubOrderExtraInfo extraInfo) {
        return JsonUtils.toJson(extraInfo);
    }

    @Named("toTaskStatusInt")
    public int toTaskStatusInt(OnceTaskStatusEnum status) {
        return status.getCode();
    }

    @Named("toTaskStatusEnum")
    public OnceTaskStatusEnum toTaskStatusEnum(int status) {
        return OnceTaskStatusEnum.findByCode(status);
    }

    @Named("toTaskTypeInt")
    public int toTaskTypeInt(TaskTypeEnum taskType) {
        return taskType.getCode();
    }

    @Named("toTaskTypeEnum")
    public TaskTypeEnum toTaskTypeEnum(int taskType) {
        return TaskTypeEnum.findByCode(taskType);
    }

    @Named("toTaskContentString")
    public String toTaskContentString(TaskContentDO content) {
        return JsonUtils.toJson(content);
    }

    @Named("toRenewTypeEnumInt")
    public int toRenewTypeEnumInt(RenewTypeEnum e) {
        return e.getCode();
    }

    @Named("toRenewTypeEnum")
    public RenewTypeEnum toRenewTypeEnumEnum(int code) {
        return RenewTypeEnum.findByCode(code);
    }

    @Named("toMemberOrderExtraInfoString")
    public String toMemberOrderExtraInfoString(MemberOrderExtraInfo o) {
        return JsonUtils.toJson(o);
    }

    @Named("toMemberOrderExtraInfo")
    public MemberOrderExtraInfo toMemberOrderExtraInfo(String json) {
        return JsonUtils.fromJson(json, MemberOrderExtraInfo.class);
    }

    @Named("toMemberOrderStatusEnumInt")
    public int toMemberOrderStatusEnumInt(MemberOrderStatusEnum e) {
        return e.getCode();
    }

    @Named("toMemberOrderStatusEnum")
    public MemberOrderStatusEnum toMemberOrderStatusEnumEnum(int code) {
        return MemberOrderStatusEnum.findByCode(code);
    }

    @Named("toMemberOrderPerformStatusEnumInt")
    public int toMemberOrderPerformStatusEnumInt(com.memberclub.domain.context.perform.common.MemberOrderPerformStatusEnum e) {
        return e.getCode();
    }

    @Named("toPurchaseSourceEnumInt")
    public int toPurchaseSourceEnumInt(SubmitSourceEnum e) {
        return e.getCode();
    }

    @Named("toPurchaseSourceEnum")
    public SubmitSourceEnum toPurchaseSourceEnumEnum(int code) {
        return SubmitSourceEnum.findByCode(code);
    }

    @Named("toMemberOrderPerformStatusEnum")
    public com.memberclub.domain.context.perform.common.MemberOrderPerformStatusEnum toMemberOrderPerformStatusEnumEnum(int code) {
        return com.memberclub.domain.context.perform.common.MemberOrderPerformStatusEnum.findByCode(code);
    }

    @Named("toRefundWayEnumInt")
    public int toRefundWayEnumInt(RefundWayEnum e) {
        return e.getCode();
    }

    @Named("toRefundWayEnum")
    public RefundWayEnum toRefundWayEnumEnum(int code) {
        return RefundWayEnum.findByCode(code);
    }

}