/**
 * @(#)OuterSubmitRecordExtraDO.java, 四月 05, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.outer;

import com.memberclub.domain.context.purchase.PurchaseSkuSubmitCmd;
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
public class OuterSubmitRecordExtraDO {

    List<PurchaseSkuSubmitCmd> skus;
}