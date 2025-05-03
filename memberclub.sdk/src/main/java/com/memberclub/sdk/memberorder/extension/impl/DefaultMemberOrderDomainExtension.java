/**
 * @(#)DefaultMemberOrderDomainExtension.java, 一月 08, 2025.
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
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.entity.trade.MemberOrder;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.mybatis.mappers.trade.MemberOrderDao;
import com.memberclub.sdk.memberorder.extension.MemberOrderDomainExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "默认 MemberOrder数据库层扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DEFAULT, scenes = SceneEnum.DEFAULT_SCENE)
})
public class DefaultMemberOrderDomainExtension implements MemberOrderDomainExtension {

    @Autowired
    private MemberOrderDao memberOrderDao;

    @Override
    public void onSubmitSuccess(MemberOrderDO memberOrderDO, LambdaUpdateWrapper<MemberOrder> wrapper) {
        int cnt = memberOrderDao.update(null, wrapper);
        return;
    }

    @Override
    public void onSubmitCancel(MemberOrderDO memberOrderDO, LambdaUpdateWrapper<MemberOrder> wrapper) {
        int cnt = memberOrderDao.update(null, wrapper);
        if (cnt <= 0) {
            throw ResultCode.DATA_UPDATE_ERROR.newException("member_order 更新到取消成功异常");
        }
        CommonLog.info("更新主单的主状态为取消完成 status:{} cnt:{}", memberOrderDO.getStatus(), cnt);
    }

    @Override
    public int onStartPerform(PerformContext context, LambdaUpdateWrapper<MemberOrder> wrapper) {
        int cnt = memberOrderDao.update(null, wrapper);
        return cnt;
    }

    @Override
    @Transactional
    public void onPerformSuccess(PerformContext context, MemberOrderDO memberOrderDO, LambdaUpdateWrapper<MemberOrder> wrapper) {
        int cnt = memberOrderDao.update(null, wrapper);
        if (cnt <= 0) {
            throw ResultCode.DATA_UPDATE_ERROR.newException("member_order 更新到履约成功异常");
        }
        CommonLog.info("更新主单的履约状态为履约完成 status:{} cnt:{}", memberOrderDO.getPerformStatus(), cnt);
    }


    @Override
    @Transactional
    public void onReversePerformSuccess(ReversePerformContext context, MemberOrderDO memberOrderDO,
                                        LambdaUpdateWrapper<MemberOrder> wrapper) {
        int cnt = memberOrderDao.update(null, wrapper);
        if (cnt < 1) {
            throw ResultCode.DATA_UPDATE_ERROR.newException("MemberOrder onReversePerformSuccess 更新异常");
        }
        CommonLog.info("更新主单的履约状态为逆向履约完成 status:{} cnt:{}", memberOrderDO.getPerformStatus(), cnt);
    }

    @Override
    @Transactional
    public void onPrePay(MemberOrderDO memberOrderDO, LambdaUpdateWrapper<MemberOrder> wrapper) {
        int cnt = memberOrderDao.update(null, wrapper);
        if (cnt < 1) {
            throw ResultCode.DATA_UPDATE_ERROR.newException("MemberOrder onPrePay 更新异常");
        }
        CommonLog.info("更新主单的支付状态为待支付 status:{} cnt:{}", memberOrderDO.getPaymentInfo().getPayStatus().getCode(), cnt);
    }

    @Transactional
    @Override
    public void onPaySuccess(MemberOrderDO memberOrderDO, LambdaUpdateWrapper<MemberOrder> wrapper) {
        int cnt = memberOrderDao.update(null, wrapper);
        if (cnt < 1) {
            throw ResultCode.DATA_UPDATE_ERROR.newException("MemberOrder onPaySuccess 更新异常");
        }
        CommonLog.info("更新主单的支付状态为支付成功 status:{} cnt:{}", memberOrderDO.getPaymentInfo().getPayStatus().getCode(), cnt);
    }

    @Transactional
    @Override
    public void onPaySuccess4OrderTimeout(MemberOrderDO memberOrderDO, LambdaUpdateWrapper<MemberOrder> wrapper) {
        int cnt = memberOrderDao.update(null, wrapper);
        if (cnt < 1) {
            throw ResultCode.DATA_UPDATE_ERROR.newException("MemberOrder onPaySuccess4OrderTimeout 更新异常");
        }
        CommonLog.info("更新主单的支付状态为支付成功 status:{} cnt:{}", memberOrderDO.getPaymentInfo().getPayStatus().getCode(), cnt);
    }

    @Transactional
    @Override
    public void onRefund4OrderTimeout(MemberOrderDO memberOrderDO, LambdaUpdateWrapper<MemberOrder> wrapper) {
        int cnt = memberOrderDao.update(null, wrapper);
        if (cnt < 1) {
            throw ResultCode.DATA_UPDATE_ERROR.newException("MemberOrder onRefund4OrderTimeout 更新异常");
        }
        CommonLog.info("更新主单的支付状态为退款 status:{} cnt:{}", memberOrderDO.getPaymentInfo().getPayStatus().getCode(), cnt);
    }
}