/**
 * @(#)MemberOrderExtraInfo.java, 十二月 29, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.order;

import com.memberclub.domain.dataobject.CommonUserInfo;
import lombok.Data;

/**
 * author: 掘金五阳
 */
@Data
public class MemberOrderExtraInfo {

    private LocationInfo locationInfo;

    private CommonUserInfo userInfo;

    private MemberOrderFinanceInfo settleInfo;

    private MemberOrderSaleInfo saleInfo;

    private Long lockValue;

    private Long startTime;
}