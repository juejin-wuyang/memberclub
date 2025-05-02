package com.memberclub.infrastructure.payment.impl;

import com.memberclub.infrastructure.payment.PaymentFacadeSPI;
import com.memberclub.infrastructure.payment.context.PrePayRequestDTO;
import com.memberclub.infrastructure.payment.context.PrePayResponseDTO;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "memberclub.infrastructure.payment", havingValue = "local")
public class MockPaymentFacadeSPI implements PaymentFacadeSPI {

    @Override
    public PrePayResponseDTO createPayOrder(PrePayRequestDTO prePayRequestDTO) {
        PrePayResponseDTO dto = new PrePayResponseDTO();
        String payTradeNo = RandomStringUtils.randomAlphabetic(10);
        dto.setUrl("http://localhost:8080/memberclub/payment/prepay?tradeNo=" + payTradeNo);
        dto.setCode(0);
        dto.setPayToken(RandomStringUtils.randomAlphanumeric(10));
        dto.setTradeNo(payTradeNo);
        return dto;
    }
}
