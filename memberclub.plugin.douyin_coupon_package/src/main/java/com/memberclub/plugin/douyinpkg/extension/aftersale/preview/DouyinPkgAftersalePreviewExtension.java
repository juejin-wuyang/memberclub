package com.memberclub.plugin.douyinpkg.extension.aftersale.preview;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.flow.FlowChain;
import com.memberclub.common.flow.FlowChainService;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewContext;
import com.memberclub.sdk.aftersale.extension.preview.AftersalePreviewExtension;
import com.memberclub.sdk.aftersale.flow.preview.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "抖音券包售后预览扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DOUYIN_COUPON_PACKAGE, scenes = {SceneEnum.SCENE_AFTERSALE_MONTH_CARD})
})
public class DouyinPkgAftersalePreviewExtension implements AftersalePreviewExtension {

    private FlowChain<AfterSalePreviewContext> previewChain = null;

    private FlowChain<AfterSalePreviewContext> subPreviewChain = null;

    @Autowired
    private FlowChainService flowChainService;

    @PostConstruct
    public void init() {
        subPreviewChain = FlowChain.newChain(AfterSalePreviewContext.class)
                .addNode(AfterSaleUsageAmountCompute4RealtimeFlow.class)            //实时计算使用类型
                .addNode(AfterSaleUsageTypeComputeFlow.class)               //完全检查使用类型
                .addNode(AftersaleRefundWayComputeFlow.class)                  //计算赔付类型
                .addNode(AftersalePlanDigestGenerateFlow.class)         //生成售后计划摘要
        ;

        previewChain = FlowChain.newChain(AfterSalePreviewContext.class)
                .addNode(AftersalePreviewDegradeFlow.class)
                // TODO: 2025/1/1  //增加售后单 进行中校验,当前存在生效中受理单,不允许预览(数据处于不一致状态,无法获得准确的预览结果),返回特殊错误码
                .addNode(AftersaleOrderMainStatusValidatePreviewFlow.class)
                .addNode(AftersalePeriodValidateFlow.class)
                .addNode(AftersaleTimesValidateFlow.class)
                .addNodeWithSubNodes(MutilSubOrderPreviewFlow.class, subPreviewChain)
        ;
    }

    @Override
    public void preview(AfterSalePreviewContext context) {
        flowChainService.execute(previewChain, context);
    }
}
