/**
 * @(#)PurchaseConvertor.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.controller.convertor;

import com.memberclub.common.util.CollectionUtilEx;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.purchase.PurchaseSubmitCmd;
import com.memberclub.domain.context.purchase.common.PurchaseSourceEnum;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.sdk.util.PriceUtils;
import com.memberclub.starter.controller.vo.PurchaseSubmitVO;
import com.memberclub.starter.controller.vo.purchase.BuyRecordVO;
import com.memberclub.starter.controller.vo.purchase.BuySubOrderVO;
import com.memberclub.starter.util.SecurityUtil;

import java.util.ArrayList;
import java.util.List;

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

    public static List<BuyRecordVO> toBuyRecordVOS(List<MemberOrderDO> orders) {
        return CollectionUtilEx.mapToList(orders, o -> toBuyRecordVOS(o));
    }

    public static BuyRecordVO toBuyRecordVOS(MemberOrderDO order) {
        BuyRecordVO record = new BuyRecordVO();
        record.setTradeId(order.getTradeId());
        List<BuySubOrderVO> subOrderVOS = new ArrayList<>();
        for (MemberSubOrderDO subOrder : order.getSubOrders()) {
            BuySubOrderVO subOrderVO = new BuySubOrderVO();
            subOrderVO.setBuyCount(subOrder.getBuyCount());
            subOrderVO.setTitle(subOrder.getExtra().getViewInfo().getDisplayName());
            subOrderVO.setSubTradeId(String.valueOf(subOrder.getSubTradeId()));
            subOrderVO.setPayPrice(PriceUtils.change2Yuan(subOrder.getExtra().getSaleInfo().getSalePriceFen()));
            subOrderVO.setBuyTime(TimeUtil.format(subOrder.getCtime()));
            subOrderVO.setEffectiveTime(
                    String.format("%s - %s", TimeUtil.formatDay(subOrder.getStime()), TimeUtil.formatDay(subOrder.getEtime()))
            );
            subOrderVOS.add(subOrderVO);
        }
        record.setSubOrders(subOrderVOS);
        return record;
    }
}