/**
 * @(#)AftersaleAmountExtension.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.extension.preview;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;
import com.memberclub.domain.context.aftersale.contant.RefundWayEnum;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewContext;
import com.memberclub.domain.context.aftersale.preview.ItemUsage;

import java.util.Map;

/**
 * author: 掘金五阳
 */
@ExtensionConfig(desc = "售后金额计算扩展点", type = ExtensionType.AFTERSALE, must = false)
public interface AftersaleAmountExtension extends BaseExtension {

    public int computeRefundPrice(AfterSalePreviewContext context, Map<String, ItemUsage> batchCode2ItemUsageMap);

    public void computeUsageTypeByAmount(AfterSalePreviewContext context);

    public RefundWayEnum computeRefundWay(AfterSalePreviewContext context);

}