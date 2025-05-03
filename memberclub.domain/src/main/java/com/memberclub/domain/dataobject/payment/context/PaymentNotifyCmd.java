package com.memberclub.domain.dataobject.payment.context;

import lombok.Data;

@Data
public class PaymentNotifyCmd {

    private long userId;

    private String tradeId;

    private String payTradeNo;

    private String payAccount;

    private Integer payAmountFen;

    private String payAccountType;

    private String payChannelType;

    private Long payTime;
}
