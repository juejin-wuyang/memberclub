package com.memberclub.infrastructure.payment.context;

import lombok.Data;

@Data
public class PrePayRequestDTO {

    private String productName;

    private String productDesc;

    private String orderId;

    private Integer amountFen;

    private long userId;

    private long payExpireTime;

    private String merchantId;


}
