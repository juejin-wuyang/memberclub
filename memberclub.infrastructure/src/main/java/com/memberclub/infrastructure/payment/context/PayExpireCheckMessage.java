package com.memberclub.infrastructure.payment.context;

import lombok.Data;

@Data
public class PayExpireCheckMessage {

    private int bizType;

    private long userId;

    private String tradeId;

    private long payExpireTime;
}
