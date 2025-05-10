package com.memberclub.sdk.aftersale.flow.preview;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.aftersale.contant.AftersaleSourceEnum;
import com.memberclub.domain.context.aftersale.contant.RefundTypeEnum;
import com.memberclub.domain.context.aftersale.contant.RefundWayEnum;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewContext;
import com.memberclub.domain.dataobject.payment.PayStatusEnum;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.sdk.aftersale.extension.preview.AftersalePreviewExtension;


@ExtensionProvider(desc = "默认的售后预览场景，服务于仅退款", bizScenes = {
        @Route(bizType = BizTypeEnum.DEFAULT, scenes = {SceneEnum.SCENE_SYSTEM_REFUND_4_ORDER_PAY_TIMEOUT})
})
public class AfterSale4OnlyRefundPreviewExtension implements AftersalePreviewExtension {

    @Override
    public void preview(AfterSalePreviewContext context) {
        if (context.getCmd().getSource() == AftersaleSourceEnum.SYSTEM_REFUND_4_ORDER_PAY_TIMEOUT) {
            MemberOrderDO memberOrderDO = context.getMemberOrder();
            if (memberOrderDO.getPaymentInfo().getPayStatus() != PayStatusEnum.PAY_SUCCESS) {
                CommonLog.warn("当前订单支付状态非支付成功，不可再次调用售后退款");
                return;
            }

            //设置原路赔付
            context.setRecommendRefundPrice(memberOrderDO.getPaymentInfo().getPayAmountFen());
            context.setRefundType(RefundTypeEnum.ALL_REFUND);
            context.setRefundWay(RefundWayEnum.ORDER_BACKSTRACK);
        }
    }
}
