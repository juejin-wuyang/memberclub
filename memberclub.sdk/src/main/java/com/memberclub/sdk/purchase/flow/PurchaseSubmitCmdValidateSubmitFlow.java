package com.memberclub.sdk.purchase.flow;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.dataobject.sku.SkuInfoDO;
import com.memberclub.domain.exception.ResultCode;
import org.springframework.stereotype.Service;

@Service
public class PurchaseSubmitCmdValidateSubmitFlow extends FlowNode<PurchaseSubmitContext> {

    @Override
    public void process(PurchaseSubmitContext purchaseSubmitContext) {
        BizTypeEnum bizTypeEnum = purchaseSubmitContext.getBizType();
        for (SkuInfoDO skuInfo : purchaseSubmitContext.getSkuInfos()) {
            if (skuInfo.getBizType() != bizTypeEnum.getCode()) {
                throw ResultCode.PARAM_VALID.newException("业务线输入错误");
            }
        }
    }
}
