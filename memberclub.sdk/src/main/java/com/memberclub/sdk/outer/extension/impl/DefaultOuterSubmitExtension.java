package com.memberclub.sdk.outer.extension.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.collect.ImmutableList;
import com.memberclub.common.flow.EmptySubFlowNode;
import com.memberclub.common.flow.FlowChain;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.dataobject.outer.OuterSubmitContext;
import com.memberclub.domain.entity.trade.OuterSubmitRecord;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.mybatis.mappers.trade.OuterSubmitRecordDao;
import com.memberclub.sdk.outer.extension.OuterSubmitExtension;
import com.memberclub.sdk.outer.flow.OuterSubmitOrderFlow;
import com.memberclub.sdk.outer.flow.OuterSubmitRecordFlow;
import com.memberclub.sdk.outer.flow.Perform4OuterSubmitFlow;
import com.memberclub.sdk.outer.service.OuterSubmitDataObjectService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class DefaultOuterSubmitExtension implements OuterSubmitExtension {

    public FlowChain<OuterSubmitContext> flowChain;

    @Autowired
    private OuterSubmitDataObjectService outerSubmitDataObjectService;

    @Autowired
    private OuterSubmitRecordDao outerSubmitRecordDao;

    protected void defualtInit() {
        flowChain = FlowChain.newChain(OuterSubmitContext.class)

                .addNode(OuterSubmitRecordFlow.class)                           //创建外部提单记录
                .addEmptyNodeWithSubNodes(OuterSubmitSubNode.class, OuterSubmitContext.class,
                        ImmutableList.of(OuterSubmitOrderFlow.class))           //记录外部提单记录，调用提单流程
                .addEmptyNodeWithSubNodes(OuterSubmitSubNode.class, OuterSubmitContext.class,
                        ImmutableList.of(Perform4OuterSubmitFlow.class))     //调用履约流程
        ;
    }

    @Override
    public void submit(OuterSubmitContext context) {
        flowChain.execute(context);
    }

    @Override
    public void onCreated(OuterSubmitContext context) {
        OuterSubmitRecord record = outerSubmitDataObjectService.buildRecord(context.getRecord());
        int cnt = outerSubmitRecordDao.insert(record);
        if (cnt <= 0) {
            CommonLog.error("创建外部提单记录失败 context:{}", context);
            throw ResultCode.COMMON_ORDER_SUBMIT_ERROR.newException("创建外部提单记录失败");
        }
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

    public static class OuterSubmitSubNode extends EmptySubFlowNode<OuterSubmitContext> {
    }
}
