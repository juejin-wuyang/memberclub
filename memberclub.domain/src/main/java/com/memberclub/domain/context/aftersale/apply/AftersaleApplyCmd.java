/**
 * @(#)AftersaleApplyCmd.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.aftersale.apply;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.aftersale.contant.AftersaleSourceEnum;
import com.memberclub.domain.dataobject.aftersale.ApplySkuInfoDO;
import com.memberclub.domain.exception.ResultCode;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Data
public class AftersaleApplyCmd {

    private BizTypeEnum bizType;

    private long userId;

    private String tradeId;

    private AftersaleSourceEnum source;

    private Long afterSaleId;

    /**
     * 目前不支持指定商品退
     */
    @Nullable
    private List<ApplySkuInfoDO> applySkus;

    private String operator;

    private String reason;

    private String previewToken;
    

    public void isValid() {
        if (StringUtils.isBlank(previewToken)) {
            throw ResultCode.PARAM_VALID.newException("缺失previewToken");
        }
    }
}