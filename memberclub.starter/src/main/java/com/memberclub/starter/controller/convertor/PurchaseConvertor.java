/**
 * @(#)PurchaseConvertor.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.controller.convertor;

import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.purchase.PurchaseSubmitCmd;
import com.memberclub.domain.context.purchase.common.PurchaseSourceEnum;
import com.memberclub.starter.controller.vo.PurchaseSubmitVO;
import com.memberclub.starter.util.SecurityUtil;

/**
 * author: 掘金五阳
 */
public class PurchaseConvertor {

    public static PurchaseSubmitCmd toSubmitCmd(PurchaseSubmitVO param) {
        PurchaseSubmitCmd cmd = new PurchaseSubmitCmd();
        cmd.setUserInfo(param.getUserInfo());
        cmd.setUserId(SecurityUtil.getUserId());
        cmd.setBizType(BizTypeEnum.findByCode(param.getBizId()));
        cmd.setClientInfo(param.getClientInfo());
        cmd.setSubmitToken(param.getSubmitToken());
        cmd.setSource(PurchaseSourceEnum.findByCode(param.getSubmitSource()));
        cmd.setSkus(param.getSkus());
        cmd.setLocationInfo(param.getLocationInfo());
        return cmd;
    }
}