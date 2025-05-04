package com.memberclub.infrastructure.payment;

import com.memberclub.infrastructure.payment.context.PaymentRefundRequestDTO;
import com.memberclub.infrastructure.payment.context.PaymentRefundResponseDTO;
import com.memberclub.infrastructure.payment.context.PrePayRequestDTO;
import com.memberclub.infrastructure.payment.context.PrePayResponseDTO;

public interface PaymentFacadeSPI {

    public PrePayResponseDTO createPayOrder(PrePayRequestDTO prePayRequestDTO);

    public PaymentRefundResponseDTO refundPayOrder(PaymentRefundRequestDTO paymentRefundRequestDTO);
}
