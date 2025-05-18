package com.memberclub.domain.context.aftersale.preview;

import com.memberclub.domain.context.aftersale.contant.RefundWayEnum;
import lombok.Data;

@Data
public class AfterSalePreviewCoreResult {

    private int recommendRefundPrice;

    private RefundWayEnum refundWay;

    private long expireTime;
}
