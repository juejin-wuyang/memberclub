/**
 * @(#)TestFlow.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.util;

import com.google.common.collect.ImmutableList;
import com.memberclub.common.flow.FlowChain;
import com.memberclub.common.flow.FlowChainService;
import com.memberclub.starter.AppStarter;
import com.memberclub.starter.mock.MockBaseTest;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * author: 掘金五阳
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AppStarter.class})
public class TestFlow extends MockBaseTest {


    private FlowChain<FlowContext> flowChainAndSub = null;
    @Autowired
    private FlowChainService flowChainService;
    private FlowChain<FlowContext> flowChain = null;

    @Before
    public void init() {


        flowChain = FlowChain.newChain(flowChainService, FlowContext.class)
                .addNode(FlowC.class)
                .addNode(FlowD.class);

        flowChainAndSub = FlowChain.newChain(flowChainService, FlowContext.class)
                .addNodeWithSubNodes(FlowC.class, SubFlowContext.class, ImmutableList.of(FlowC1.class, FlowC2.class))
                .addNode(FlowD.class);
    }


}