package com.memberclub.sdk.aftersale.flow.preview;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.aftersale.contant.AftersaleSourceEnum;
import com.memberclub.domain.context.aftersale.contant.RefundTypeEnum;
import com.memberclub.domain.context.aftersale.contant.RefundWayEnum;
import com.memberclub.domain.context.aftersale.contant.UsageTypeEnum;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewContext;
import com.memberclub.domain.context.aftersale.preview.ItemUsage;
import com.memberclub.domain.dataobject.payment.PayStatusEnum;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.sdk.aftersale.extension.preview.AftersalePreviewExtension;
import com.memberclub.sdk.aftersale.service.domain.AfterSaleDomainService;
import com.memberclub.sdk.perform.service.domain.PerformDomainService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ExtensionProvider(desc = "默认的售后预览场景，服务于履约失败回滚资源", bizScenes = {
        @Route(bizType = BizTypeEnum.DEFAULT, scenes = {SceneEnum.SCENE_SYSTEM_REFUND_4_PERFORM_FAIL})
})
public class AfterSale4PerformFailFallbackPreviewExtension implements AftersalePreviewExtension {

    @Autowired
    private PerformDomainService performDomainService;

    @Autowired
    private AfterSaleDomainService afterSaleDomainService;

    @Override
    public void preview(AfterSalePreviewContext context) {
        if (context.getCmd().getSource() == AftersaleSourceEnum.SYSTEM_REFUND_4_PERFORM_FAIL) {
            MemberOrderDO memberOrderDO = context.getMemberOrder();
            if (memberOrderDO.getPaymentInfo().getPayStatus() != PayStatusEnum.PAY_SUCCESS) {
                CommonLog.warn("当前订单支付状态非支付成功，不可再次调用售后退款");
                return;
            }

            //设置原路赔付
            context.setRecommendRefundPrice(memberOrderDO.getPaymentInfo().getPayAmountFen());
            context.setRefundType(RefundTypeEnum.ALL_REFUND);
            context.setRefundWay(RefundWayEnum.ORDER_BACKSTRACK);
            context.setPreviewToken(afterSaleDomainService.generatePreviewToken(context.getCmd().getSource(), context.getCmd().getTradeId()));

            List<MemberPerformItemDO> items = performDomainService.queryItemsByTradeId(context.getCmd().getUserId(), context.getCmd().getTradeId());
            Map<String, ItemUsage> itemToken2ItemUsage = items.stream().collect(Collectors.toMap(MemberPerformItemDO::getItemToken, this::newItemUsage));
            context.setItemToken2ItemUsage(itemToken2ItemUsage);
        }

    }

    public ItemUsage newItemUsage(MemberPerformItemDO itemDO) {
        ItemUsage itemUsage = new ItemUsage();
        itemUsage.setUsageType(UsageTypeEnum.UNUSE);
        return itemUsage;
    }
}
