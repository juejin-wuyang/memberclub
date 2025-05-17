package com.memberclub.domain.context.aftersale.apply;

import com.memberclub.domain.context.aftersale.contant.RefundTypeEnum;
import com.memberclub.domain.context.aftersale.contant.RefundWayEnum;
import com.memberclub.domain.context.aftersale.preview.ItemUsage;
import lombok.Data;

import java.util.Map;

@Data
public class AfterSaleExecuteCmd {

    private AftersaleApplyCmd applyCmd;

    private RefundWayEnum refundWay;

    private RefundTypeEnum refundType;

    private int recommendRefundPrice = 0;

    private String scene;

    private Integer periodIndex;

    private Map<String, ItemUsage> itemToken2ItemUsage;
}
