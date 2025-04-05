/**
 * @(#)OuterSubmitContext.java, 四月 05, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.outer;

import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author: 掘金五阳
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OuterSubmitContext {
    private long userId;

    private BizTypeEnum bizType;

    private OuterSubmitCmd cmd;

    private OuterSubmitRecordDO record;

    private MemberOrderDO memberOrder;
}