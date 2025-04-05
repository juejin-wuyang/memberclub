/**
 * @(#)SubmitOrderDTO.java, 一月 05, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.order.context;

import com.memberclub.domain.dataobject.CommonUserInfo;
import lombok.Data;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Data
public class SubmitOrderRequestDTO {

    private long userId;

    private CommonUserInfo userInfo;

    private String submitToken;

    private List<SkuBuyInfoDTO> skus;

    private int orderType;

    private Boolean zeroPay;
}