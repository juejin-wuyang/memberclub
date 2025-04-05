/**
 * @(#)PurchaseSubmitContext.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.purchase;

import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.dataobject.CommonUserInfo;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.dataobject.sku.SkuInfoDO;
import com.memberclub.domain.exception.MemberException;
import lombok.Data;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Data
public class PurchaseSubmitContext {

    /********************************************/
    //临时数据

    int orderSubmitErrorCode;
    String orderSubmitMsg;
    /*********************************************/
    private long userId;
    //基础入参
    private CommonUserInfo userInfo;
    private BizTypeEnum bizType;
    private PurchaseSubmitCmd submitCmd;
    private Long lockValue;
    private Boolean zeroPay;
    /********************************************/
    //模型数据

    private MemberOrderDO memberOrder;
    private List<SkuInfoDO> skuInfos;

    private Long renewStime;

    /***
     * 待生效或生效中订单
     */
    private List<MemberOrderDO> nonExpiredMemberOrderDOS;


    /********************************************/
    //
    public PurchaseSubmitContext(PurchaseSubmitCmd cmd) {
        this.submitCmd = cmd;
        this.userId = cmd.getUserId();
        this.bizType = cmd.getBizType();
        this.userInfo = cmd.getUserInfo();
        this.zeroPay = cmd.getZeroPay();
    }


    /********************************************/
    public BizScene toDefaultBizScene() {
        return BizScene.of(bizType, submitCmd.getSource().getCode() + "");
    }


    public void monitor() {
        // TODO: 2025/1/4 补充监控
    }

    public void monitorException(MemberException e) {
        // TODO: 2025/1/4 补充监控
    }


}