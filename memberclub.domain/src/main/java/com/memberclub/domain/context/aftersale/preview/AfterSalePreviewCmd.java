/**
 * @(#)AfterSalePreviewCmd.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.aftersale.preview;

import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.aftersale.contant.AftersaleSourceEnum;
import lombok.Data;

/**
 * author: 掘金五阳
 */
@Data
public class AfterSalePreviewCmd {

    private BizTypeEnum bizType;

    private long userId;

    private String tradeId;

    private AftersaleSourceEnum source;

    private String operator;

    private boolean previewBeforeApply;
}