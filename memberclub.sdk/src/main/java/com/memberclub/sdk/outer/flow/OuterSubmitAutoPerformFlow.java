package com.memberclub.sdk.outer.flow;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.retry.Retryable;
import com.memberclub.domain.context.perform.PerformResp;
import com.memberclub.domain.dataobject.outer.OuterSubmitContext;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.sdk.outer.service.OuterSubmitDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OuterSubmitAutoPerformFlow extends FlowNode<OuterSubmitContext> {

    @Autowired
    private OuterSubmitDomainService outerSubmitDomainService;

    @Autowired
    private OuterSubmitAutoPerformFlow outerSubmitAutoPerformFlow;

    @Override
    public void process(OuterSubmitContext context) {
        try {
            outerSubmitDomainService.perform(context);
        } catch (Exception e) {
            CommonLog.warn("调用履约异常 context:{}", context, e);
            outerSubmitAutoPerformFlow.retryPerform(context);
        }
    }

    @Retryable(throwException = true, initialDelaySeconds = 1, maxTimes = 5)
    public void retryPerform(OuterSubmitContext context) {
        PerformResp resp = outerSubmitDomainService.perform(context);
        if (resp.isSuccess()) {
            success(context);
            return;
        }
        if (resp.isNeedRetry()) {
            throw ResultCode.PERFORM_ERROR.newException("履约失败需要重试");
        }
        CommonLog.error("履约失败，不需要重试 resp:{} context:{}", resp, context);
    }

    @Override
    public void success(OuterSubmitContext context) {
        outerSubmitDomainService.onPerformSuccess(context);
    }
}
