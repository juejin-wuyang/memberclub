/**
 * @(#)MockCommonOrderFacadeSPI.java, 一月 05, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.order.facade;

import com.google.common.collect.Lists;
import com.memberclub.domain.dataobject.purchase.facade.SkuBuyResultDO;
import com.memberclub.infrastructure.order.context.SkuBuyInfoDTO;
import com.memberclub.infrastructure.order.context.SubmitOrderRequestDTO;
import com.memberclub.infrastructure.order.context.SubmitOrderResponseDTO;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 */
@Service
@ConditionalOnProperty(name = "memberclub.infrastructure.order", havingValue = "local")
public class MockCommonOrderFacadeSPI implements CommonOrderFacadeSPI {

    public static final Logger LOG = LoggerFactory.getLogger(MockCommonOrderFacadeSPI.class);


    @Override
    public SubmitOrderResponseDTO submit(SubmitOrderRequestDTO request) {
        SubmitOrderResponseDTO result = new SubmitOrderResponseDTO();
        result.setSuccess(true);
        result.setOrderId(RandomUtils.nextLong() + "");

        List<SkuBuyResultDO> results = Lists.newArrayList();

        for (SkuBuyInfoDTO sku : request.getSkus()) {
            SkuBuyResultDO skuBuyResultDO = new SkuBuyResultDO();
            skuBuyResultDO.setActPriceFen((sku.getSalePrice()) * sku.getBuyCount());
            skuBuyResultDO.setSkuId(sku.getSkuId());
            skuBuyResultDO.setBuyCount(sku.getBuyCount());
            results.add(skuBuyResultDO);
        }
        result.setSkuBuyResults(results);
        result.setActPriceFen(results.stream().collect(Collectors.summingInt(SkuBuyResultDO::getActPriceFen)));

        return result;
    }

    @Override
    public RefundOrderResponseDTO refund(RefundOrderRequestDTO request) {
        LOG.info("订单收到退款请求:{}", request);
        RefundOrderResponseDTO dto = new RefundOrderResponseDTO();
        dto.setSuccess(true);
        dto.setOrderRefundId(RandomUtils.nextLong() + "");
        return dto;
    }
}