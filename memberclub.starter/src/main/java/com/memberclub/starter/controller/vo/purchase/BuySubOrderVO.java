package com.memberclub.starter.controller.vo.purchase;

import lombok.Data;

@Data
public class BuySubOrderVO {
    private String title;

    private String subTradeId;

    private String effectiveTime;

    private String buyTime;

    private String payPrice;

    private Integer buyCount;
}
