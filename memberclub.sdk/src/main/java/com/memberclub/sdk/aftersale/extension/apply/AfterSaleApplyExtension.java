/**
 * @(#)AfterSaleApplyExtension.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.extension.apply;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.dataobject.aftersale.AftersaleOrderDO;

/**
 * author: 掘金五阳
 */
@ExtensionConfig(desc = "售后受理扩展点", type = ExtensionType.AFTERSALE, must = true)
public interface AfterSaleApplyExtension extends BaseExtension {
    public void apply(AfterSaleApplyContext context);

    public void doApply(AfterSaleApplyContext context);

    public void customBuildAftersaleOrder(AfterSaleApplyContext context, AftersaleOrderDO aftersaleOrderDO);

    public void apply4OnlyRefundMoney(AfterSaleApplyContext context);
}