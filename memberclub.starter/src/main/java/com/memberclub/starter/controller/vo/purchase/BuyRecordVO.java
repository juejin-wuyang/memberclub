package com.memberclub.starter.controller.vo.purchase;

import lombok.Data;

import java.util.List;

@Data
public class BuyRecordVO {

    private String tradeId;

    private List<BuySubOrderVO> subOrders;
}
