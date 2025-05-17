/**
 * @(#)AftersaleOrderDO.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.aftersale;

import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.aftersale.contant.AftersaleSourceEnum;
import com.memberclub.domain.context.aftersale.contant.RefundTypeEnum;
import com.memberclub.domain.context.aftersale.contant.RefundWayEnum;
import lombok.Data;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Data
public class AftersaleOrderDO {

    private BizTypeEnum bizType;

    private long userId;

    private AftersaleSourceEnum source;

    private Long id;

    private String operator;

    private String reason;

    private String tradeId;

    private String previewToken;

    private List<ApplySkuInfoDO> applySkuInfos;

    private AftersaleOrderExtraDO extra;

    private Integer actPayPriceFen;

    private Integer actRefundPriceFen;

    private Integer recommendRefundPriceFen;

    private AftersaleOrderStatusEnum status;

    private RefundTypeEnum refundType;

    private RefundWayEnum refundWay;

    private long utime;

    private long ctime;


    public void onAfterSaleSuccess(AfterSaleApplyContext context) {
        status = AftersaleOrderStatusEnum.AFTERSALE_SUCCESS;
        setUtime(System.currentTimeMillis());
    }

    public void onPerformReversed(AfterSaleApplyContext context) {
        status = AftersaleOrderStatusEnum.REVERSE_PERFORM_SUCCESS;
    }

    public void onPurchaseReversed(AfterSaleApplyContext context) {
        status = AftersaleOrderStatusEnum.REVERSE_PURCHASE_SUCCESS;
    }

    public void onOrderRefunfSuccess(AfterSaleApplyContext context) {
        status = AftersaleOrderStatusEnum.REFUND_ORDER_SUCCESS;
    }
}