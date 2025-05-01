package com.memberclub.sdk.aftersale.flow.doapply;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.sdk.quota.service.aftersale.AftersaleQuotaDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AftersaleUserQuotaFlow extends FlowNode<AfterSaleApplyContext> {
    @Autowired
    private AftersaleQuotaDomainService aftersaleQuotaDomainService;

    @Override
    public void process(AfterSaleApplyContext afterSaleApplyContext) {
        aftersaleQuotaDomainService.onApply(afterSaleApplyContext);
    }
}
