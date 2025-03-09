/**
 * @(#)SPICommonOrderExtension.java, 一月 05, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.purchase.extension.impl;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.OrderSystemTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.dataobject.purchase.facade.CommonOrderRefundResult;
import com.memberclub.domain.dataobject.purchase.facade.CommonOrderSubmitResult;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.order.context.SkuBuyInfoDTO;
import com.memberclub.infrastructure.order.context.SubmitOrderRequestDTO;
import com.memberclub.infrastructure.order.context.SubmitOrderResponseDTO;
import com.memberclub.infrastructure.order.facade.CommonOrderFacadeSPI;
import com.memberclub.infrastructure.order.facade.RefundOrderRequestDTO;
import com.memberclub.infrastructure.order.facade.RefundOrderResponseDTO;
import com.memberclub.sdk.purchase.extension.CommonOrderExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 * 基于 SPI 实现,实际项目中应该替换具体的订单接口(订单更通用,会员很难替订单定义接口格式)
 * 为了演示方便,设计 SPI 接口和实现类.
 */
@ExtensionProvider(desc = "通用订单交互扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DEMO_MEMBER, scenes = {SceneEnum.DEFAULT_SCENE}),
        @Route(bizType = BizTypeEnum.DOUYIN_COUPON_PACKAGE, scenes = {SceneEnum.DEFAULT_SCENE}),
        @Route(bizType = BizTypeEnum.LESSON, scenes = {SceneEnum.DEFAULT_SCENE}),
})
public class SPICommonOrderExtension implements CommonOrderExtension {


    @Autowired
    private CommonOrderFacadeSPI commonOrderFacadeSPI;

    @Override
    public void onPreSubmit(PurchaseSubmitContext context) {
        context.getMemberOrder().getOrderInfo().setOrderType(1);//各业务各场景 单独定义
        context.getMemberOrder().getOrderInfo().setOrderSystemType(OrderSystemTypeEnum.COMMON_ORDER);
    }

    @Override
    public CommonOrderSubmitResult doSubmit(PurchaseSubmitContext context) {
        SubmitOrderRequestDTO request = new SubmitOrderRequestDTO();
        request.setOrderType(context.getMemberOrder().getOrderInfo().getOrderType());
        request.setUserId(context.getUserId());
        request.setUserInfo(context.getUserInfo());
        request.setSubmitToken(context.getSubmitCmd().getSubmitToken());

        List<SkuBuyInfoDTO> dtos = context.getSkuInfos().stream()
                .map(i -> {
                    SkuBuyInfoDTO dto = new SkuBuyInfoDTO();
                    dto.setSkuId(i.getSkuId());
                    dto.setBuyCount(i.getBuyCount());
                    dto.setSalePrice(i.getSaleInfo().getSalePriceFen());
                    return dto;
                }).collect(Collectors.toList());
        request.setSkus(dtos);

        SubmitOrderResponseDTO response = null;
        try {
            response = commonOrderFacadeSPI.submit(request);
        } catch (Exception e) {
            CommonLog.error("调用订单提单异常 request:{}", request, e);
            throw ResultCode.COMMON_ORDER_SUBMIT_ERROR.newException("调用订单提单异常", e);
        }
        if (response.isSuccess()) {
            CommonLog.info("调用 CommonOrder 提单成功, request:{}, response:{}", request, response);
            CommonOrderSubmitResult result = new CommonOrderSubmitResult();
            result.setOrderId(response.getOrderId());
            result.setSkuBuyResults(response.getSkuBuyResults());
            result.setActPriceFen(response.getActPriceFen());
            return result;
        }

        context.setOrderSubmitErrorCode(response.getErrorCode());
        context.setOrderSubmitMsg(response.getMsg());
        throw ResultCode.COMMON_ORDER_SUBMIT_ERROR.newException(String.format("调用订单提单失败 errorCode:%s, msg:%s",
                response.getErrorCode(), response.getMsg()
        ));
    }

    @Override
    public CommonOrderRefundResult refund(AfterSaleApplyContext context) {
        RefundOrderRequestDTO request = new RefundOrderRequestDTO();
        request.setOrderId(context.getPreviewContext().getMemberOrder().getOrderInfo().getOrderId());
        request.setRefundPriceFen(context.getOrderRefundPriceFen());
        request.setRefundType(context.getAftersaleOrderDO().getRefundType().getCode());
        request.setUserId(context.getAftersaleOrderDO().getUserId());
        RefundOrderResponseDTO response = commonOrderFacadeSPI.refund(request);
        if (response.isSuccess()) {
            CommonOrderRefundResult result = new CommonOrderRefundResult();
            result.setOrderRefundId(response.getOrderRefundId());
            return result;
        }

        context.setErrorCode(response.getErrorCode());
        context.setErrorMsg(response.getErrorMsg());

        throw ResultCode.COMMON_ORDER_SUBMIT_ERROR.newException(
                String.format("调用订单退款失败 errorCode:%s, msg:%s",
                        response.getErrorCode(), response.getErrorMsg()
                ));
    }
}