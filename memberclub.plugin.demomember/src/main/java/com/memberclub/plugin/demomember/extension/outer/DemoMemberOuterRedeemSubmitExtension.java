package com.memberclub.plugin.demomember.extension.outer;

import com.google.common.collect.ImmutableList;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.flow.FlowChain;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.dataobject.outer.OuterSubmitContext;
import com.memberclub.sdk.outer.extension.impl.DefaultOuterSubmitExtension;
import com.memberclub.sdk.outer.flow.OuterSubmitOrderFlow;
import com.memberclub.sdk.outer.flow.OuterSubmitRecordFlow;
import com.memberclub.sdk.outer.flow.Perform4OuterSubmitFlow;
import com.memberclub.sdk.outer.flow.RedeemExchangeFlow;

import javax.annotation.PostConstruct;

@ExtensionProvider(desc = "DemoMember 外部兑换码下单扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DEMO_MEMBER, scenes = {SceneEnum.OUTER_SUBMIT_REDEEM_SCENE})
})
public class DemoMemberOuterRedeemSubmitExtension extends DefaultOuterSubmitExtension {

    @PostConstruct
    public void init() {
        flowChain = FlowChain.newChain(OuterSubmitContext.class)
                .addNode(RedeemExchangeFlow.class)                              //兑换流程
                .addNode(OuterSubmitRecordFlow.class)                           //创建外部提单记录
                .addEmptyNodeWithSubNodes(OuterSubmitSubNode.class, OuterSubmitContext.class,
                        ImmutableList.of(OuterSubmitOrderFlow.class))           //记录外部提单记录，调用提单流程
                .addEmptyNodeWithSubNodes(OuterSubmitSubNode.class, OuterSubmitContext.class,
                        ImmutableList.of(Perform4OuterSubmitFlow.class))     //调用履约流程
        ;
    }
}
