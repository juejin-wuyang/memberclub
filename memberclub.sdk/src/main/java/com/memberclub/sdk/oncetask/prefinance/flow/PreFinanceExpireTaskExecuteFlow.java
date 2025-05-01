/**
 * @(#)ExpirePreFinanceTaskExecuteFlow.java, 一月 28, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.oncetask.prefinance.flow;

import com.google.common.collect.Lists;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.flow.FlowNode;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.oncetask.execute.OnceTaskExecuteContext;
import com.memberclub.domain.context.perform.common.SubOrderPerformStatusEnum;
import com.memberclub.domain.context.prefinance.task.FinanceTaskContent;
import com.memberclub.domain.dataobject.event.trade.TradeEventDO;
import com.memberclub.domain.dataobject.event.trade.TradeEventDetailDO;
import com.memberclub.domain.dataobject.event.trade.TradeEventEnum;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.sdk.prefinance.extension.PreFinanceHandleExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class PreFinanceExpireTaskExecuteFlow extends FlowNode<OnceTaskExecuteContext> {

    public static final Logger LOG = LoggerFactory.getLogger(PreFinanceExpireTaskExecuteFlow.class);


    @Autowired
    private ExtensionManager extensionManager;

    @Override
    public void process(OnceTaskExecuteContext context) {
        TradeEventDO event = buildEvent(context);
        extensionManager.getExtension(BizScene.of(event.getDetail().getBizType()),
                PreFinanceHandleExtension.class).handle(event);
    }

    private TradeEventDO buildEvent(OnceTaskExecuteContext context) {
        try {
            TradeEventDO eventDO = new TradeEventDO();
            eventDO.setEventType(TradeEventEnum.MEMBER_PERFORM_ITEM_EXPIRE);

            FinanceTaskContent content = ((FinanceTaskContent) context.getOnceTask().getContent());

            TradeEventDetailDO detail = new TradeEventDetailDO();
            detail.setBizType(context.getBizType());
            detail.setEventTime(TimeUtil.now());
            detail.setItemTokens(Lists.newArrayList(context.getOnceTask().getTaskToken()));
            detail.setPerformStatus(SubOrderPerformStatusEnum.PERFORM_SUCCESS);
            detail.setPeriodIndex(content.getPhase());
            detail.setSubTradeId(Long.valueOf(content.getSubTradeId()));
            detail.setTradeId(context.getOnceTask().getTradeId());
            detail.setUserId(context.getOnceTask().getUserId());
            detail.setSkuId(content.getSkuId());

            eventDO.setDetail(detail);
            return eventDO;
        } catch (Exception e) {
            LOG.info("解析构建 TradeEventDO 异常:{}", context, e);
            throw ResultCode.EXTRACT_MESSAGE_ERROR.newException("解析TradeEventDO异常", e);
        }
    }
}