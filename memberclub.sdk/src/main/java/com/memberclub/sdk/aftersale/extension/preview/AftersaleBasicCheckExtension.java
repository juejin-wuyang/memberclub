/**
 * @(#)AftersalePreviewCheckExtension.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.extension.preview;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.context.aftersale.contant.AftersaleUnableCode;
import com.memberclub.domain.context.aftersale.preview.AftersalePreviewContext;
import com.memberclub.domain.context.perform.common.MemberOrderPerformStatusEnum;
import com.memberclub.domain.context.purchase.common.MemberOrderStatusEnum;

/**
 * author: 掘金五阳
 */
@ExtensionConfig(desc = "售后预览基础校验扩展点", type = ExtensionType.AFTERSALE, must = true)
public interface AftersaleBasicCheckExtension extends BaseExtension {
    default void statusCheck(AftersalePreviewContext context) {
        MemberOrderStatusEnum status = context.getMemberOrder().getStatus();
        MemberOrderPerformStatusEnum performStatus = context.getMemberOrder().getPerformStatus();

        CommonLog.info("当前订单状态:{}", status.toString(), performStatus.toString());
        if (status == MemberOrderStatusEnum.COMPLETE_REFUNDED) {
            throw AftersaleUnableCode.REFUNDED.newException();
        }

        if (MemberOrderStatusEnum.nonPerformed(status.getCode())) {
            throw AftersaleUnableCode.NON_PERFORMED.newException();
        }

        if (MemberOrderStatusEnum.PORTION_REFUNDED == status) {
            CommonLog.info("当前会员订单已部分退可以再次申请售后");
        }
    }

    default void validatePeriod(AftersalePreviewContext context) {
        context.setStime(context.getMemberOrder().getStime());
        context.setEtime(context.getMemberOrder().getEtime());
        if (context.getEtime() < TimeUtil.now()) {
            CommonLog.info("当前订单已过期不能退 stime:{}, etime:{}", context.getStime(), context.getEtime());
            throw AftersaleUnableCode.EXPIRE_ERROR.newException();
        }
        CommonLog.info("当前订单有效期 stime:{}, etime:{}", context.getStime(), context.getEtime());
    }
}