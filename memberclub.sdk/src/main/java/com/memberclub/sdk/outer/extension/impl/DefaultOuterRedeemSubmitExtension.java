package com.memberclub.sdk.outer.extension.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.memberclub.common.flow.EmptySubFlowNode;
import com.memberclub.common.flow.FlowChain;
import com.memberclub.domain.dataobject.outer.OuterSubmitContext;
import com.memberclub.domain.entity.trade.OuterSubmitRecord;
import com.memberclub.infrastructure.mybatis.mappers.trade.OuterSubmitRecordDao;
import com.memberclub.sdk.outer.extension.OuterSubmitExtension;
import com.memberclub.sdk.outer.service.OuterSubmitDataObjectService;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultOuterRedeemSubmitExtension implements OuterSubmitExtension {

    private FlowChain<OuterSubmitContext> flowChain;

    @Autowired
    private OuterSubmitDataObjectService outerSubmitDataObjectService;

    @Autowired
    private OuterSubmitRecordDao outerSubmitRecordDao;

    @Override
    public void submit(OuterSubmitContext context) {

    }

    @Override
    public void onCreated(OuterSubmitContext context) {

    }

    @Override
    public void onSubmitSuccess(OuterSubmitContext context, LambdaUpdateWrapper<OuterSubmitRecord> wrapper) {
        outerSubmitRecordDao.update(null, wrapper);
    }

    @Override
    public void onSubmitFail(OuterSubmitContext context, LambdaUpdateWrapper<OuterSubmitRecord> wrapper) {
        outerSubmitRecordDao.update(null, wrapper);
    }

    @Override
    public void onPerformSuccess(OuterSubmitContext context, LambdaUpdateWrapper<OuterSubmitRecord> wrapper) {
        outerSubmitRecordDao.update(null, wrapper);
    }

    public static class OuterPurchaseSubmitSubNode extends EmptySubFlowNode<OuterSubmitContext> {
    }
}
