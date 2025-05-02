package com.memberclub.sdk.payment;

import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.dataobject.payment.PaymentDO;
import com.memberclub.domain.dataobject.payment.PrePayResult;
import com.memberclub.infrastructure.payment.context.PrePayRequestDTO;
import com.memberclub.infrastructure.payment.context.PrePayResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class PaymentDataObjectFactory {

    public PrePayRequestDTO createPrePayRequestDTO(PurchaseSubmitContext context) {
        PrePayRequestDTO prePayRequestDTO = new PrePayRequestDTO();
        PaymentDO paymentDO = context.getMemberOrder().getPaymentInfo();
        prePayRequestDTO.setUserId(context.getUserId());
        prePayRequestDTO.setMerchantId(paymentDO.getMerchantId());
        prePayRequestDTO.setAmountFen(context.getMemberOrder().getActPriceFen());
        prePayRequestDTO.setOrderId(context.getMemberOrder().getTradeId());
        prePayRequestDTO.setProductName(paymentDO.getProductName());
        prePayRequestDTO.setProductDesc(paymentDO.getProductDesc());
        prePayRequestDTO.setPayExpireTime(context.getPayExpireTime());
        return prePayRequestDTO;
    }

    public PrePayResult convertPayResponse(PrePayResponseDTO prePayResponseDTO) {
        PrePayResult result = new PrePayResult();
        result.setUrl(prePayResponseDTO.getUrl());
        result.setPayToken(prePayResponseDTO.getPayToken());
        result.setTradeNo(prePayResponseDTO.getTradeNo());
        return result;
    }
}
