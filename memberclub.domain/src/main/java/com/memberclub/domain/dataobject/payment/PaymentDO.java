package com.memberclub.domain.dataobject.payment;

import lombok.Data;

@Data
public class PaymentDO {

    //支付状态
    private PayStatusEnum payStatus;

    //商户编码
    private String merchantId;

    //支付流水号
    private String payTradeNo;

    //支付时间
    private Long payTime;

    //支付金额
    private Integer payAmountFen;

    //支付账号
    private String payAccount;//支付账号

    //支付节点，货到付款、到期支付、立即支付
    private PayNodeTypeEnum payNodeType;

    //在线支付
    private PayOnlineTypeEnum payOnlineType;

    //支付渠道类型
    private String payChannelType;

    //支付账户类型，为了避免 跟随支付系统升级，使用字符串承载。（其实订单系统不太关心支付的账户类型）
    private String payAccountType;

    private String productName;

    private String productDesc;
}
