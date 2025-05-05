package com.memberclub.sdk.purchase.service;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.dataobject.sku.SkuInfoDO;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.amountcompute.AmountComputeProductDetailResult;
import com.memberclub.infrastructure.amountcompute.AmountComputeRequestDTO;
import com.memberclub.infrastructure.amountcompute.AmountComputeResponseDTO;
import com.memberclub.infrastructure.amountcompute.AmountComputeSPI;
import com.memberclub.infrastructure.dynamic_config.SwitchEnum;
import com.memberclub.sdk.purchase.extension.AmountComputeExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AmountComputeService {

    @Autowired
    private AmountComputeSPI amountComputeSPI;

    @Autowired
    private ExtensionManager extensionManager;

    private static int summingSalePrice(PurchaseSubmitContext context) {
        int salePriceFenSum = 0;
        for (SkuInfoDO skuInfo : context.getSkuInfos()) {
            salePriceFenSum += skuInfo.getSaleInfo().getSalePriceFen();
        }
        return salePriceFenSum;
    }

    private static void computeAmount4Exception(PurchaseSubmitContext context, int salePriceFenSum) {
        context.getMemberOrder().setActPriceFen(salePriceFenSum);
        for (MemberSubOrderDO subOrder : context.getMemberOrder().getSubOrders()) {
            for (SkuInfoDO skuInfoDO : context.getSkuInfos()) {
                if (subOrder.getSkuId() == skuInfoDO.getSkuId()) {
                    subOrder.setActPriceFen(skuInfoDO.getSaleInfo().getSalePriceFen());
                }
            }
        }
    }

    private static void computeAmount4Success(PurchaseSubmitContext context, AmountComputeResponseDTO response, AmountComputeRequestDTO request) {
        context.getMemberOrder().setActPriceFen(response.getTotalAmountFen());

        CommonLog.warn("优惠金额计算结果:{}, request:{}", response, request);
        for (MemberSubOrderDO subOrder : context.getMemberOrder().getSubOrders()) {
            for (AmountComputeProductDetailResult detail : response.getDetails()) {
                if (subOrder.getSkuId() == detail.getSkuId()) {
                    subOrder.setActPriceFen(detail.getAmountFen());
                }
            }
        }
    }

    public void amountCompute(PurchaseSubmitContext context) {
        AmountComputeExtension extension = extensionManager.getExtension(context.toDefaultBizScene(), AmountComputeExtension.class);
        AmountComputeRequestDTO request = extension.buildRequest(context);

        try {
            AmountComputeResponseDTO response = amountComputeSPI.compute(request);
            if (response.isSuccess()) {
                //设置应付金额
                computeAmount4Success(context, response, request);
                return;
            }
        } catch (Exception e) {
            CommonLog.error("调用优惠计算异常 request:{}", request);
        }

        //异常兜底流程
        if (SwitchEnum.AMOUNT_COMPUTE_EXCEPTION_SKIP_ENABLE.getBoolean(context.getBizType().getCode())) {
            int salePriceFenSum = summingSalePrice(context);
            CommonLog.warn("调用优惠计算异常，因此使用售卖价提单 salePriceFenSum:{}", salePriceFenSum);
            computeAmount4Exception(context, salePriceFenSum);
        } else {
            throw ResultCode.AMOUNT_COMPUTE_EXCEPTION.newException();
        }
    }
}
