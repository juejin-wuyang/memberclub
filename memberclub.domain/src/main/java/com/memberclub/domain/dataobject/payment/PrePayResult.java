package com.memberclub.domain.dataobject.payment;

import lombok.Data;

@Data
public class PrePayResult {

    /**
     * 跳转 url
     */
    private String url;

    private String tradeNo;

    private String payToken;

    private int payAmountFen;
/*

    //支付时间
    private Long payTime;
    //支付账号
    private String payAccount;//支付账号
    //支付渠道类型
    private String payChannelType;
    //支付账户类型，为了避免 跟随支付系统升级，使用字符串承载。（其实订单系统不太关心支付的账户类型）
    private String payAcccountTypeEnum;*/
}
