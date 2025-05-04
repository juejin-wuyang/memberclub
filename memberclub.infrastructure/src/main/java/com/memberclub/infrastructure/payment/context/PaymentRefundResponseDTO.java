package com.memberclub.infrastructure.payment.context;

import lombok.Data;

@Data
public class PaymentRefundResponseDTO {

    private int code;

    private String msg;

    private int refundAmountFen;


    public boolean isSuccess() {
        return code == 0;
    }
}
