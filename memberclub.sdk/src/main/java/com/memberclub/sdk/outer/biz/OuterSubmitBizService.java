package com.memberclub.sdk.outer.biz;

import com.memberclub.domain.dataobject.outer.OuterSubmitCmd;
import com.memberclub.domain.dataobject.outer.OuterSubmitContext;
import com.memberclub.domain.dataobject.outer.OuterSubmitResponse;
import com.memberclub.sdk.outer.service.OuterSubmitDataObjectService;
import com.memberclub.sdk.outer.service.OuterSubmitDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OuterSubmitBizService {

    @Autowired
    private OuterSubmitDomainService outerSubmitDomainService;

    @Autowired
    private OuterSubmitDataObjectService outerSubmitDataObjectService;

    public OuterSubmitResponse submit(OuterSubmitCmd cmd) {
        OuterSubmitResponse response = new OuterSubmitResponse();
        cmd.isValid();

        OuterSubmitContext context = outerSubmitDataObjectService.buildContext(cmd);

        outerSubmitDomainService.submit(context);
        if (context.getMemberOrder() != null) {
            response.setTradeId(context.getMemberOrder().getTradeId());
        }

        return response;
    }
}
