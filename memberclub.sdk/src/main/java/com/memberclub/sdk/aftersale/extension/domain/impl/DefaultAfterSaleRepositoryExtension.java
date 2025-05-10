/**
 * @(#)DefaultAftersaleDomainExtension.java, 一月 11, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.extension.domain.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.dataobject.aftersale.AftersaleOrderDO;
import com.memberclub.domain.entity.trade.AftersaleOrder;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.mybatis.mappers.trade.AftersaleOrderDao;
import com.memberclub.sdk.aftersale.extension.domain.AfterSaleRepositoryExtension;
import com.memberclub.sdk.common.Monitor;
import org.springframework.beans.factory.annotation.Autowired;

import static com.memberclub.domain.dataobject.aftersale.AftersaleOrderStatusEnum.AFTERSALE_SUCCESS;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "默认 AftersaleOrder 数据库层扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DEFAULT, scenes = SceneEnum.DEFAULT_SCENE)
})
public class DefaultAfterSaleRepositoryExtension implements AfterSaleRepositoryExtension {

    @Autowired
    private AftersaleOrderDao aftersaleOrderDao;

    @Override
    public void onSuccess(AfterSaleApplyContext context, AftersaleOrderDO order, LambdaUpdateWrapper<AftersaleOrder> wrapper) {
        int cnt = aftersaleOrderDao.update(null, wrapper);

        if (cnt < 1) {
            AftersaleOrder orderFromDb = aftersaleOrderDao.queryById(order.getUserId(), order.getId());
            if (orderFromDb != null && AFTERSALE_SUCCESS.getCode() == orderFromDb.getStatus()) {
                CommonLog.warn("修改售后单为成功态,幂等成功 order:{}", order);
                Monitor.AFTER_SALE_DOAPPLY.counter(order.getBizType(),
                        "onAftersaleSuccess", "duplicated");
            } else {
                CommonLog.warn("修改售后单为成功态, 失败 order:{}", order);
                Monitor.AFTER_SALE_DOAPPLY.counter(order.getBizType(),
                        "onAftersaleSuccess", "error");
                throw ResultCode.DATA_UPDATE_ERROR.newException("更新售后单为成功态异常");
            }
        } else {
            CommonLog.warn("修改售后单为成功态成功 order:{}", order);
            Monitor.AFTER_SALE_DOAPPLY.counter(order.getBizType(),
                    "onAftersaleSuccess", "succ");
        }
    }

    @Override
    public void onRefundSuccess(AfterSaleApplyContext context, AftersaleOrderDO order, LambdaUpdateWrapper<AftersaleOrder> wrapper) {
        int cnt = aftersaleOrderDao.update(null, wrapper);
        CommonLog.warn("修改售后单为退款成功态成功 status:{} cnt:{}", order.getStatus(), cnt);
    }
}