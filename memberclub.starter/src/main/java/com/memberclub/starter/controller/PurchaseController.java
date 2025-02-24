/**
 * @(#)PurchaseController.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.controller;

import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.context.purchase.PurchaseSubmitCmd;
import com.memberclub.domain.context.purchase.PurchaseSubmitResponse;
import com.memberclub.domain.exception.MemberException;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.sku.SkuBizService;
import com.memberclub.sdk.purchase.service.biz.PurchaseBizService;
import com.memberclub.starter.controller.convertor.PurchaseConvertor;
import com.memberclub.starter.controller.vo.PurchaseSubmitVO;
import com.memberclub.starter.controller.vo.purchase.PurchaseSubmitResponseVO;
import com.memberclub.starter.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: 掘金五阳
 */
@RestController()
@RequestMapping("/memberclub/purchase")
public class PurchaseController {


    @Autowired
    private PurchaseBizService purchaseBizService;

    @Autowired
    private SkuBizService skuBizService;

    @PostMapping("/submit")
    public PurchaseSubmitResponseVO submit(HttpServletRequest servletRequest, @RequestBody PurchaseSubmitVO param) {
        PurchaseSubmitResponseVO response = new PurchaseSubmitResponseVO();
        try {
            param.isValid();//通用参数校验
            SecurityUtil.securitySet(servletRequest);
            PurchaseSubmitCmd cmd = PurchaseConvertor.toSubmitCmd(param);
            PurchaseSubmitResponse resp = purchaseBizService.submit(cmd);
            response.setSucc(resp.isSuccess());
            if (resp.isSuccess()) {
                response.setTradeId(resp.getMemberOrderDO().getTradeId());
            }
            response.setErrorCode(resp.getErrorCode());
            response.setErrorMsg(resp.getMsg());
        } catch (MemberException e) {
            CommonLog.error("提单异常 param:{}", param, e);
            response.setSucc(false);
            response.setErrorCode(e.getCode().getCode());
            response.setErrorMsg(e.getCode().getMsg());
        } catch (Throwable e) {
            CommonLog.error("提单异常 param:{}", param, e);
            response.setSucc(false);
            response.setErrorCode(ResultCode.INTERNAL_ERROR.getCode());
            response.setErrorMsg(ResultCode.INTERNAL_ERROR.getMsg());
        }
        return response;
    }
}