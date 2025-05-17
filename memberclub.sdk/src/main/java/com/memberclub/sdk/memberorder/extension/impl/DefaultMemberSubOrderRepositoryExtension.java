/**
 * @(#)DefaultMemberSubOrderDomainExtension.java, 一月 08, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.memberorder.extension.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.SubOrderPerformContext;
import com.memberclub.domain.context.perform.common.SubOrderPerformStatusEnum;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.domain.context.perform.reverse.SubOrderReversePerformContext;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.entity.trade.MemberSubOrder;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.mybatis.mappers.trade.MemberSubOrderDao;
import com.memberclub.sdk.memberorder.extension.MemberSubOrderRepositoryExtension;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "默认 MemberSubOrder数据库层扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DEFAULT, scenes = SceneEnum.DEFAULT_SCENE)
})
public class DefaultMemberSubOrderRepositoryExtension implements MemberSubOrderRepositoryExtension {

    @Autowired
    private MemberSubOrderDao memberSubOrderDao;

    @Override
    public void onSubmitSuccess(MemberSubOrderDO memberSubOrderDO, LambdaUpdateWrapper<MemberSubOrder> wrapper) {
        memberSubOrderDao.update(null, wrapper);
    }

    @Override
    public void onSubmitCancel(MemberSubOrderDO memberSubOrderDO, LambdaUpdateWrapper<MemberSubOrder> wrapper) {
        int cnt = memberSubOrderDao.update(null, wrapper);
        if (cnt < 1) {
            throw ResultCode.DATA_UPDATE_ERROR.newException("MemberSubOrder onSubmitCancel 更新异常");
        }
        CommonLog.info("更新子单状态为取消 status:{}, cnt:{}", memberSubOrderDO.getStatus(), cnt);

    }

    @Override
    public void onStartPerform(PerformContext context,
                               SubOrderPerformContext subOrderPerformContext,
                               MemberSubOrderDO subOrder,
                               LambdaUpdateWrapper<MemberSubOrder> wrapper) {
        int cnt = memberSubOrderDao.update(null, wrapper);

        if (cnt > 0) {
            CommonLog.warn("修改会员子单履约状态为履约中: {}", subOrder);
            return;
        }
        MemberSubOrder subOrderFromDb = memberSubOrderDao.selectBySkuId(context.getUserId(),
                context.getTradeId(), subOrderPerformContext.getSubOrder().getSkuId());
        if (subOrderFromDb == null) {
            CommonLog.error("member_sub_order缺失!", subOrder);
            throw ResultCode.INTERNAL_ERROR.newException(String.format("找不到 member_sub_order subTradeId:%s", subOrder.getSubTradeId()));
        }

        if (SubOrderPerformStatusEnum.hasPerformed(subOrderFromDb.getPerformStatus())) {
            CommonLog.error(" member_sub_order 已履约完成,无需再次履约:{}", subOrder);
            // TODO: 2024/12/15 外层已经过滤,一般不会走到这里.
            return;
        }
        CommonLog.error("重试请求,继续履约子单:{}", subOrder);
    }

    @Override
    public void onPerformSuccess(PerformContext performContext,
                                 SubOrderPerformContext subOrderPerformContext,
                                 MemberSubOrderDO memberSubOrderDO,
                                 LambdaUpdateWrapper<MemberSubOrder> wrapper) {
        int cnt = memberSubOrderDao.update(null, wrapper);
        if (cnt < 1) {
            throw ResultCode.DATA_UPDATE_ERROR.newException("MemberSubOrder onPerformSuccess 更新异常");
        }
        CommonLog.info("更新子单状态为履约完成 status:{}, cnt:{}", memberSubOrderDO.getPerformStatus(), cnt);
    }

    @Override
    public void onStartReversePerform(ReversePerformContext context, SubOrderReversePerformContext subOrderReversePerformContext, MemberSubOrderDO subOrder,
                                      LambdaUpdateWrapper<MemberSubOrder> wrapper) {
        int cnt = memberSubOrderDao.update(null, wrapper);
        if (cnt < 1) {
            throw ResultCode.DATA_UPDATE_ERROR.newException("MemberSubOrder onStartReversePerform 更新异常");
        }
        CommonLog.info("更新履约单状态为逆向履约中 status:{}, cnt:{}", subOrder.getPerformStatus(), cnt);
    }

    @Override
    public void onReversePerformSuccess(ReversePerformContext context, SubOrderReversePerformContext subOrderReversePerformContext, MemberSubOrderDO subOrder,
                                        LambdaUpdateWrapper<MemberSubOrder> wrapper) {
        int cnt = memberSubOrderDao.update(null, wrapper);
        if (cnt < 1) {
            //throw ResultCode.DATA_UPDATE_ERROR.newException("MemberSubOrder onReversePerformSuccess 更新异常");
            CommonLog.error("onReversePerformSuccess 更新子单状态为逆向履约完成 status:{}异常, cnt:{}", subOrder.getPerformStatus(), cnt);
            return;
        }
        CommonLog.info("更新子单状态为逆向履约完成 status:{}, cnt:{}", subOrder.getPerformStatus(), cnt);
    }

    @Override
    public void onRefundSuccess(AfterSaleApplyContext context, MemberSubOrderDO subOrder, LambdaUpdateWrapper<MemberSubOrder> wrapper) {
        int cnt = memberSubOrderDao.update(null, wrapper);
        if (cnt < 1) {
            //throw ResultCode.DATA_UPDATE_ERROR.newException("MemberSubOrder onRefundSuccess 更新异常");
            CommonLog.error("onRefundSuccess 更新子单状态为退款完成 status:{}异常, cnt:{}", subOrder.getStatus(), cnt);
            return;
        }
        CommonLog.info("更新子单状态为退款完成 status:{}, cnt:{}", subOrder.getStatus(), cnt);
    }
}