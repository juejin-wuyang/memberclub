/**
 * @(#)MemberPerformHisDO.java, 十二月 28, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.perform;

import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.OrderSystemTypeEnum;
import com.memberclub.domain.context.perform.SubOrderPerformContext;
import com.memberclub.domain.context.perform.common.SubOrderPerformStatusEnum;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.domain.context.perform.reverse.SubOrderReversePerformContext;
import com.memberclub.domain.context.purchase.common.SubOrderStatusEnum;
import com.memberclub.domain.dataobject.payment.context.PaymentNotifyContext;
import com.memberclub.domain.dataobject.perform.his.SubOrderExtraInfo;
import com.memberclub.domain.dataobject.sku.SkuPerformConfigDO;
import lombok.Data;

/**
 * author: 掘金五阳
 */
@Data
public class MemberSubOrderDO {

    private BizTypeEnum bizType;

    private long userId;

    private OrderSystemTypeEnum relatedOrderSystemType;

    private String relatedOrderId;

    private String tradeId;//会员单交易 ID

    private Long subTradeId;

    private int buyCount;

    private long skuId;

    private Integer actPriceFen;

    private Integer originPriceFen;

    private Integer salePriceFen;

    private SubOrderStatusEnum status;

    private SubOrderPerformStatusEnum performStatus;

    private SubOrderExtraInfo extra = new SubOrderExtraInfo();

    private SkuPerformConfigDO performConfig;

    private long stime;

    private long etime;

    private long utime;

    private long ctime;


    public void onStartPerform(SubOrderPerformContext subOrderPerformContext) {
        performStatus = SubOrderPerformStatusEnum.PERFORMING;
        setUtime(System.currentTimeMillis());
    }

    public void onPaymentSuccess(PaymentNotifyContext context, MemberSubOrderDO subOrder) {
        setStatus(SubOrderStatusEnum.PAYED);
        setUtime(System.currentTimeMillis());
    }

    public void onPerformSuccess(SubOrderPerformContext subOrderPerformContext) {
        setStatus(SubOrderStatusEnum.PERFORMED);
        setPerformStatus(SubOrderPerformStatusEnum.PERFORM_SUCCESS);
        setUtime(System.currentTimeMillis());
    }

    public void onStartReversePerform(ReversePerformContext context, SubOrderReversePerformContext subOrderReversePerformContext) {
        performStatus = SubOrderPerformStatusEnum.REVEREING;
        setUtime(System.currentTimeMillis());
    }


    public void onReversePerformSuccess(boolean allRefund) {
        SubOrderPerformStatusEnum newPerformStatus = allRefund ? SubOrderPerformStatusEnum.COMPLETED_REVERSED :
                SubOrderPerformStatusEnum.PORTION_REVERSED;
        performStatus = newPerformStatus;
        setUtime(System.currentTimeMillis());
    }
}