/**
 * @(#)OuterSubmitDomainService.java, 四月 05, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.outer.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.flow.SkipException;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.perform.PerformCmd;
import com.memberclub.domain.context.perform.PerformResp;
import com.memberclub.domain.context.purchase.PurchaseSubmitCmd;
import com.memberclub.domain.context.purchase.PurchaseSubmitResponse;
import com.memberclub.domain.dataobject.aftersale.ClientInfo;
import com.memberclub.domain.dataobject.order.LocationInfo;
import com.memberclub.domain.dataobject.outer.OuterSubmitContext;
import com.memberclub.domain.dataobject.outer.OuterSubmitRecordDO;
import com.memberclub.domain.dataobject.outer.OuterSubmitStatusEnum;
import com.memberclub.domain.entity.trade.OuterSubmitRecord;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.mybatis.mappers.trade.OuterSubmitRecordDao;
import com.memberclub.sdk.outer.extension.OuterSubmitExtension;
import com.memberclub.sdk.perform.service.PerformBizService;
import com.memberclub.sdk.purchase.service.biz.PurchaseBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class OuterSubmitDomainService {

    @Autowired
    private ExtensionManager extensionManager;

    @Autowired
    private PurchaseBizService purchaseBizService;

    @Autowired
    private PerformBizService performBizService;

    @Autowired
    private OuterSubmitDataObjectService outerSubmitDataObjectService;
    @Autowired
    private OuterSubmitRecordDao outerSubmitRecordDao;

    public static BizScene toBizScene(OuterSubmitContext context) {
        return BizScene.of(context.getBizType(), context.getCmd().getOuterType().getCode() + "");
    }

    public void submit(OuterSubmitContext context) {
        OuterSubmitExtension extension = extensionManager.getExtension(toBizScene(context), OuterSubmitExtension.class);
        extension.submit(context);
    }

    public void submitOrder(OuterSubmitContext context) {
        PurchaseSubmitCmd cmd = new PurchaseSubmitCmd();
        cmd.setUserInfo(context.getCmd().getUserInfo());
        cmd.setUserId(context.getUserId());
        cmd.setBizType(context.getBizType());
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setClientCode(-1);//未知客户端类型
        clientInfo.setClientName("outer_client");
        cmd.setClientInfo(clientInfo);
        cmd.setSubmitToken(null);
        cmd.setSource(context.getCmd().getOuterType());
        cmd.setSkus(context.getCmd().getSkus());
        cmd.setZeroPay(true);

        LocationInfo locationInfo = new LocationInfo();

        cmd.setLocationInfo(locationInfo);
        PurchaseSubmitResponse response = purchaseBizService.submit(cmd);

        if (response.isSuccess()) {
            context.setMemberOrder(response.getMemberOrderDO());
        } else {
            throw ResultCode.COMMON_ORDER_SUBMIT_ERROR.newException("提单异常");
        }
    }

    public PerformResp perform(OuterSubmitContext context) {
        PerformCmd cmd = new PerformCmd();
        cmd.setBizType(context.getBizType());
        cmd.setUserId(context.getUserId());
        cmd.setTradeId(context.getMemberOrder().getTradeId());

        return performBizService.perform(cmd);
    }


    public void onCreated(OuterSubmitContext context) {
        OuterSubmitRecord record = outerSubmitRecordDao.selectByOutId(context.getUserId(), context.getCmd().getOuterId());
        if (record == null) {
            OuterSubmitRecordDO recordDO = outerSubmitDataObjectService.buildRecord(context);
            context.setRecord(recordDO);
            extensionManager.getExtension(toBizScene(context), OuterSubmitExtension.class).onCreated(context);
            return;
        }
        if (record.getStatus() >= OuterSubmitStatusEnum.SUBMIT_SUCCESS.getCode()) {
            throw new SkipException("已提单成功，无需再次重试");
        }
        if (record.getStatus() == OuterSubmitStatusEnum.SUBMIT_FAIL.getCode()) {
            throw ResultCode.COMMON_ORDER_SUBMIT_ERROR.newException("该笔订单提单失败，无需重试");
        }
        throw ResultCode.OUTER_SUBMIT_RETRY.newException("请重试");
    }

    public void onSubmitSuccess(OuterSubmitContext context) {
        context.getRecord().onSubmitSuccess(context);

        LambdaUpdateWrapper<OuterSubmitRecord> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(OuterSubmitRecord::getUserId, context.getUserId());
        wrapper.eq(OuterSubmitRecord::getOuterId, context.getCmd().getOuterId());
        wrapper.set(OuterSubmitRecord::getStatus, context.getRecord().getStatus().getCode());
        wrapper.set(OuterSubmitRecord::getUtime, context.getRecord().getUtime());
        wrapper.set(OuterSubmitRecord::getTradeId, context.getRecord().getTradeId());

        extensionManager.getExtension(toBizScene(context), OuterSubmitExtension.class).onSubmitSuccess(context, wrapper);
    }


    public void onSubmitFail(OuterSubmitContext context) {
        context.getRecord().onSubmitFail(context);

        LambdaUpdateWrapper<OuterSubmitRecord> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(OuterSubmitRecord::getUserId, context.getUserId());
        wrapper.eq(OuterSubmitRecord::getOuterId, context.getCmd().getOuterId());
        wrapper.set(OuterSubmitRecord::getStatus, context.getRecord().getStatus().getCode());
        wrapper.set(OuterSubmitRecord::getUtime, context.getRecord().getUtime());

        extensionManager.getExtension(toBizScene(context), OuterSubmitExtension.class).onSubmitFail(context, wrapper);
    }

    public void onPerformSuccess(OuterSubmitContext context) {
        context.getRecord().onPerformSuccess(context);
        LambdaUpdateWrapper<OuterSubmitRecord> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(OuterSubmitRecord::getUserId, context.getUserId());
        wrapper.eq(OuterSubmitRecord::getOuterId, context.getCmd().getOuterId());
        wrapper.set(OuterSubmitRecord::getStatus, context.getRecord().getStatus().getCode());
        wrapper.set(OuterSubmitRecord::getUtime, context.getRecord().getUtime());

        extensionManager.getExtension(toBizScene(context), OuterSubmitExtension.class).onPerformSuccess(context, wrapper);
    }
}