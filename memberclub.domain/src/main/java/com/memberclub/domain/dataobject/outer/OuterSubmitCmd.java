/**
 * @(#)OuterSubmitCmd.java, 四月 05, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.outer;

import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.purchase.PurchaseSkuSubmitCmd;
import com.memberclub.domain.context.purchase.common.SubmitSourceEnum;
import com.memberclub.domain.dataobject.CommonUserInfo;
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
public class OuterSubmitCmd {
    private long userId;

    private CommonUserInfo userInfo;
    /**
     * 外部下单 ID
     */
    private String outerId;

    /**
     * 外部下单配置 iD
     */
    private String outerConfigId;

    private List<PurchaseSkuSubmitCmd> skus;

    private BizTypeEnum bizType;

    /**
     * 外部下单类型
     */
    private SubmitSourceEnum outerType;

    public void isValid() {
        //todo
    }
}