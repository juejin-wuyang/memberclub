/**
 * @(#)PurchaseSubmitCmd.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.purchase;

import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.purchase.common.SubmitSourceEnum;
import com.memberclub.domain.dataobject.CommonUserInfo;
import com.memberclub.domain.dataobject.aftersale.ClientInfo;
import com.memberclub.domain.dataobject.order.LocationInfo;
import com.memberclub.domain.exception.ResultCode;
import lombok.Data;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Data
public class PurchaseSubmitCmd {

    private BizTypeEnum bizType;

    private long userId;

    private CommonUserInfo userInfo;

    private ClientInfo clientInfo;

    private LocationInfo locationInfo;

    private SubmitSourceEnum source;

    private Boolean zeroPay;

    private String submitToken;

    private List<PurchaseSkuSubmitCmd> skus;

    public void isValid() {
        if (source == null) {
            throw ResultCode.PARAM_VALID.newException("提单来源为空");
        }
    }
}