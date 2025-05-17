/**
 * @(#)AfterSalePreviewRespose.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.aftersale.preview;

import com.memberclub.domain.context.aftersale.contant.RefundTypeEnum;
import com.memberclub.domain.context.aftersale.contant.RefundWayEnum;
import lombok.Data;

/**
 * author: 掘金五阳
 */
@Data
public class AfterSalePreviewResponse {

    private boolean success;

    private RefundTypeEnum refundType;

    private RefundWayEnum refundWay;

    private String previewToken;

    private String recommendRefundPriceYuan;
    private int unableCode;
    private String unableTip;
    private String digests;
    private int digestVersion;

    public boolean isAftersaleEnabled() {
        return success && unableCode == 0;
    }
}