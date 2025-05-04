package com.memberclub.infrastructure.payment.context;

import lombok.Data;

@Data
public class PaymentRefundRequestDTO {

    private long userId;

    private String tradeNo;

    private int refundAmountFen;

    private String refundOuterNo;

    private String reason;

    private String merchantId;
}
