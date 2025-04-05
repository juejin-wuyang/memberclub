package com.memberclub.sdk.outer.flow;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.dataobject.outer.OuterSubmitContext;
import com.memberclub.sdk.outer.service.OuterSubmitDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OuterSubmitRecordFlow extends FlowNode<OuterSubmitContext> {

    @Autowired
    private OuterSubmitDomainService outerSubmitDomainService;

    @Override
    public void process(OuterSubmitContext context) {
        outerSubmitDomainService.onCreated(context);
    }
}
