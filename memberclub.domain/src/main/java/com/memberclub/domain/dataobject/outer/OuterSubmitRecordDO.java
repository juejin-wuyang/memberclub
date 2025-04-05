/**
 * @(#)OuterSubmitRecordDO.java, 四月 05, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.outer;

import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.purchase.PurchaseSkuSubmitCmd;
import com.memberclub.domain.context.purchase.common.SubmitSourceEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * author: 掘金五阳
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OuterSubmitRecordDO {

    private long userId;

    /**
     * 外部下单 ID
     */
    private String outerId;

    /**
     * 外部下单配置 iD
     */
    private String outerConfigId;

    private String tradeId;

    private List<PurchaseSkuSubmitCmd> skus;

    private OuterSubmitRecordExtraDO extra;

    private BizTypeEnum bizType;

    /**
     * 外部下单类型
     */
    private SubmitSourceEnum outerType;

    private OuterSubmitStatusEnum status;

    private long utime;

    private long ctime;

    public void onSubmitSuccess(OuterSubmitContext context) {
        status = OuterSubmitStatusEnum.SUBMIT_SUCCESS;
        utime = System.currentTimeMillis();
        tradeId = context.getMemberOrder().getTradeId();
    }

    public void onSubmitFail(OuterSubmitContext context) {
        status = OuterSubmitStatusEnum.SUBMIT_FAIL;
        utime = System.currentTimeMillis();
    }

    public void onPerformSuccess(OuterSubmitContext context) {
        status = OuterSubmitStatusEnum.PERFORMED;
        utime = System.currentTimeMillis();
    }
}