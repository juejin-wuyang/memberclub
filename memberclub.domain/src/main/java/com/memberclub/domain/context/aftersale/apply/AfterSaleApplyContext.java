/**
 * @(#)AfterSaleApplyContext.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.aftersale.apply;

import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.common.RetryableContext;
import com.memberclub.domain.dataobject.aftersale.AftersaleOrderDO;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import lombok.Data;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Data
public class AfterSaleApplyContext implements RetryableContext {


    private int retryTimes = 0;
    private String scene;

    //private AfterSalePreviewContext previewContext;
    /******************************************/

    //售后预览和入参

    private AftersaleApplyCmd applyCmd;
    private AfterSaleExecuteCmd executeCmd;
    private MemberOrderDO memberOrder;

    private List<MemberPerformItemDO> totalPerformItems;
    private List<MemberPerformItemDO> reversablePerformItems;
    private Long lockValue;//成功后释放锁

    /******************************************/
    //售后订单信息

    private AftersaleOrderDO aftersaleOrderDO;

    private Integer orderRefundPriceFen;

    private Boolean payOrderRefundInvokeSuccess;

    /******************************************/

    private int errorCode;

    private String errorMsg;

    /****************************************/

    public BizScene toBizScene() {
        return BizScene.of(applyCmd.getBizType(), scene);
    }

}