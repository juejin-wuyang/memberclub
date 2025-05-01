/**
 * @(#)QuotaExtensionContext.java, 一月 31, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.quota.extension;

import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.usertag.UserTagOpDO;
import com.memberclub.domain.context.usertag.UserTagOpTypeEnum;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import lombok.Data;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Data
public class QuotaExtensionContext {
    long userId;
    List<SkuAndRestrictInfo> skus;
    List<UserTagOpDO> userTagOpDOList;
    private BizTypeEnum bizType;
    private MemberOrderDO memberOrderDO;
    private UserTagOpTypeEnum opType;
    //限额的开始时间
    private long startTime;
}