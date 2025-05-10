/**
 * @(#)TestDemoMember.java, 十二月 21, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.demomember;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.util.*;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.aftersale.apply.AftersaleApplyCmd;
import com.memberclub.domain.context.aftersale.apply.AftersaleApplyResponse;
import com.memberclub.domain.context.aftersale.contant.AftersaleSourceEnum;
import com.memberclub.domain.context.aftersale.contant.RefundTypeEnum;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewCmd;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewResponse;
import com.memberclub.domain.context.oncetask.common.OnceTaskStatusEnum;
import com.memberclub.domain.context.oncetask.common.TaskTypeEnum;
import com.memberclub.domain.context.oncetask.trigger.OnceTaskTriggerCmd;
import com.memberclub.domain.context.perform.PerformCmd;
import com.memberclub.domain.context.perform.PerformResp;
import com.memberclub.domain.context.perform.common.PerformItemStatusEnum;
import com.memberclub.domain.context.perform.common.SubOrderPerformStatusEnum;
import com.memberclub.domain.context.purchase.PurchaseSkuSubmitCmd;
import com.memberclub.domain.context.purchase.PurchaseSubmitCmd;
import com.memberclub.domain.context.purchase.PurchaseSubmitResponse;
import com.memberclub.domain.context.purchase.common.MemberOrderStatusEnum;
import com.memberclub.domain.context.purchase.common.SubOrderStatusEnum;
import com.memberclub.domain.context.purchase.common.SubmitSourceEnum;
import com.memberclub.domain.dataobject.CommonUserInfo;
import com.memberclub.domain.dataobject.aftersale.AftersaleOrderStatusEnum;
import com.memberclub.domain.dataobject.order.LocationInfo;
import com.memberclub.domain.dataobject.order.MemberOrderExtraInfo;
import com.memberclub.domain.dataobject.outer.OuterSubmitCmd;
import com.memberclub.domain.dataobject.outer.OuterSubmitResponse;
import com.memberclub.domain.dataobject.outer.OuterSubmitStatusEnum;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.dataobject.redeem.RedeemStatusEnum;
import com.memberclub.domain.dataobject.sku.SkuInfoDO;
import com.memberclub.domain.dataobject.task.OnceTaskDO;
import com.memberclub.domain.dataobject.task.TaskContentDO;
import com.memberclub.domain.dataobject.task.perform.PerformTaskContentDO;
import com.memberclub.domain.entity.trade.*;
import com.memberclub.domain.exception.MemberException;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.domain.facade.AssetDO;
import com.memberclub.domain.facade.AssetStatusEnum;
import com.memberclub.infrastructure.dynamic_config.DynamicConfig;
import com.memberclub.infrastructure.lock.impl.LocalDistributedLock;
import com.memberclub.infrastructure.mapstruct.AftersaleConvertor;
import com.memberclub.infrastructure.mapstruct.PerformConvertor;
import com.memberclub.infrastructure.mapstruct.PerformCustomConvertor;
import com.memberclub.infrastructure.mq.MQTopicEnum;
import com.memberclub.infrastructure.mq.MessageQuenePublishFacade;
import com.memberclub.infrastructure.mq.MessageQueueDebugFacade;
import com.memberclub.infrastructure.mybatis.mappers.trade.*;
import com.memberclub.sdk.aftersale.service.AfterSaleBizService;
import com.memberclub.sdk.outer.biz.OuterSubmitBizService;
import com.memberclub.sdk.perform.service.PerformBizService;
import com.memberclub.starter.job.OnceTaskTriggerBizService;
import com.memberclub.starter.util.JustUnitTest;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 */
public class TestDemoMember extends TestDemoMemberPurchase {

    public static final Logger LOG = LoggerFactory.getLogger(TestDemoMember.class);
    @Autowired
    private MemberSubOrderDao memberSubOrderDao;
    @Autowired
    private AftersaleOrderDao aftersaleOrderDao;
    @Autowired
    private AfterSaleBizService aftersaleBizService;
    @Autowired
    private OnceTaskDao onceTaskDao;
    //@SpyBean(name = "localDistributedLock")
    private LocalDistributedLock localDistributedLock;
    @Autowired
    private DynamicConfig dynamicConfig;
    @Autowired
    private MessageQuenePublishFacade messageQuenePublishFacade;
    @Autowired
    private OnceTaskTriggerBizService onceTaskTriggerBizService;
    @Autowired
    private MemberShipDao memberShipDao;
    @Autowired
    private MemberPerformItemDao memberPerformItemDao;
    @Autowired
    private MemberOrderDao memberOrderDao;
    @Autowired
    private OuterSubmitRecordDao outerSubmitRecordDao;
    @Autowired
    private PerformBizService performBizService;
    @Autowired
    private OuterSubmitBizService outerSubmitBizService;
    @Autowired
    private RedeemDao redeemDao;

    private static Redeem buildRedeem(String code) {
        Redeem redeem = new Redeem();
        redeem.setCode(code);
        redeem.setCtime(System.currentTimeMillis());
        redeem.setUtime(System.currentTimeMillis());
        redeem.setStatus(RedeemStatusEnum.INIT.getCode());
        return redeem;
    }

    @Test
    public void testApollo() throws Exception {
        //while (true) {
        LOG.info("单测用例轮训获取参数配置");
        Thread.sleep(1000);
        if (!dynamicConfig.getBoolean("junit_test_poll_config", true)) {
            LOG.info("退出轮训");
        } else {
            Assert.fail("配置错误");
        }
        //}
    }

    @SneakyThrows
    @Test
    public void testDefaultMemberAndRetry() {
        PurchaseSubmitResponse response = submit(doubleRightsSku, 1);
        MemberOrderDO memberOrder = response.getMemberOrderDO();

        Mockito.reset(couponGrantFacade);

        Mockito.doThrow(new RuntimeException("mock error"))
                .when(couponGrantFacade).grant(Mockito.any());

        MemberOrder orderInDb = memberOrderDao.selectByTradeId(memberOrder.getUserId(), memberOrder.getTradeId());
        System.out.println(JsonUtils.toJson(orderInDb));

        PerformCmd cmd = buildCmd(memberOrder);

        try {
            PerformResp resp = performBizService.perform(cmd);
            Assert.assertTrue(resp.isSuccess());
            verifyData(cmd);
        } catch (Exception e) {
            CommonLog.error("首次调用出现异常", e);
        }

        Mockito.doCallRealMethod().when(couponGrantFacade).grant(Mockito.any());


        Thread.sleep(1500);

        verifyData(cmd);
    }

    /**
     * 重复Retryable注解
     */
    @SneakyThrows
    //@Test
    public void testDefaultMemberAndRetryRepeat() {
        PurchaseSubmitResponse response = submit(doubleRightsSku, 1);
        MemberOrderDO memberOrder = response.getMemberOrderDO();

        Mockito.reset(couponGrantFacade);

        Mockito.doThrow(new RuntimeException("mock error"))
                .when(couponGrantFacade).grant(Mockito.any());

        MemberOrder orderInDb = memberOrderDao.selectByTradeId(memberOrder.getUserId(), memberOrder.getTradeId());
        System.out.println(JsonUtils.toJson(orderInDb));

        PerformCmd cmd = buildCmd(memberOrder);

        try {
            PerformResp resp = performBizService.perform(cmd);
            Assert.assertTrue(resp.isSuccess());
            verifyData(cmd);
        } catch (Exception e) {
            CommonLog.error("首次调用出现异常", e);
        }
        Mockito.doThrow(new RuntimeException()).when(localDistributedLock).unlock(Mockito.anyString(), Mockito.anyLong());

        Mockito.doCallRealMethod().when(couponGrantFacade).grant(Mockito.any());


        Thread.sleep(1500);

        Mockito.reset(localDistributedLock);
    }

    @SneakyThrows
    @Test
    public void testDefaultMemberAndMutilPeriodCardAndPeriodPerform() {
        checkMessageAndReset(MQTopicEnum.TRADE_EVENT);
        PurchaseSubmitResponse response = submit(cycle3Sku, 1);
        MemberOrderDO memberOrder = response.getMemberOrderDO();


        MemberOrder orderInDb = memberOrderDao.selectByTradeId(memberOrder.getUserId(), memberOrder.getTradeId());
        System.out.println(JsonUtils.toJson(orderInDb));

        PerformCmd cmd = buildCmd(memberOrder);

        PerformResp resp = performBizService.perform(cmd);
        Assert.assertTrue(resp.isSuccess());
        verifyData(cmd, 1);

        verifyTaskData(cmd, cycle3Sku.getPerformConfig().getConfigs().get(0).getCycle() - 1);

        List<OnceTask> tasks = onceTaskDao.queryTasksByUserId(cmd.getUserId(), TaskTypeEnum.PERIOD_PERFORM.getCode());

        List<OnceTaskDO> taskDOS = tasks.stream().map(task -> {
            OnceTaskDO taskDO = PerformConvertor.INSTANCE.toOnceTaskDO(task);
            TaskContentDO contentDO = PerformCustomConvertor.toTaskContentDO(task.getContent(), task.getTaskContentClassName());
            taskDO.setTradeId(((PerformTaskContentDO) contentDO).getTradeId());
            taskDO.setContent(contentDO);
            return taskDO;
        }).collect(Collectors.toList());

        checkMessageAndReset(MQTopicEnum.TRADE_EVENT, (msgs) -> Assert.assertEquals(1, msgs.size()));
        for (OnceTaskDO taskDO : taskDOS) {
            performBizService.periodPerform(taskDO);
            checkMessageAndReset(MQTopicEnum.TRADE_EVENT, (msgs) -> Assert.assertEquals(1, msgs.size()));
        }
        verifyData(cmd, 3);

        //Thread.sleep(1000000);
    }

    @SneakyThrows
    @Test
    public void testDefaultMemberAndMutilPeriodCardAndPeriodPerformTrigger() {
        checkMessageAndReset(MQTopicEnum.TRADE_EVENT);

        PurchaseSubmitCmd submitCmd = buildPurchaseSubmitCmd(cycle3Sku.getSkuId(), 1);
        submitCmd.setUserId(userIdGenerator.incrementAndGet());
        PurchaseSubmitResponse response = purchaseBizService.submit(submitCmd);
        MemberOrderDO memberOrder = response.getMemberOrderDO();


        MemberOrder orderInDb = memberOrderDao.selectByTradeId(memberOrder.getUserId(), memberOrder.getTradeId());
        System.out.println(JsonUtils.toJson(orderInDb));

        PerformCmd cmd = buildCmd(memberOrder);

        PerformResp resp = performBizService.perform(cmd);
        Assert.assertTrue(resp.isSuccess());
        verifyData(cmd, 1);
        checkMessageAndReset(MQTopicEnum.TRADE_EVENT, (msgs) -> Assert.assertEquals(1, msgs.size()));
        verifyTaskData(cmd, cycle3Sku.getPerformConfig().getConfigs().get(0).getCycle() - 1);

        List<OnceTask> tasks = onceTaskDao.queryTasksByUserId(cmd.getUserId(), TaskTypeEnum.PERIOD_PERFORM.getCode());

        OnceTaskTriggerCmd triggerCmd = new OnceTaskTriggerCmd();
        triggerCmd.setUserIds(Sets.newHashSet(submitCmd.getUserId()));
        triggerCmd.setBizType(BizTypeEnum.DEMO_MEMBER);

        onceTaskTriggerBizService.triggerPeriodPerform(triggerCmd);
        checkMessageAndReset(MQTopicEnum.TRADE_EVENT, (msgs) -> Assert.assertEquals(2, msgs.size()));

        verifyData(cmd, 3);

        //Thread.sleep(1000000);
    }

    @SneakyThrows
    @Test
    public void testDefaultMemberAndMutilPeriodCardAndPeriodPerformTriggerAndExpire() {
        checkMessageAndReset(MQTopicEnum.TRADE_EVENT);

        //提单
        PurchaseSubmitCmd submitCmd = buildPurchaseSubmitCmd(cycle3Sku.getSkuId(), 1);
        submitCmd.setUserId(userIdGenerator.incrementAndGet());
        PurchaseSubmitResponse response = purchaseBizService.submit(submitCmd);
        MemberOrderDO memberOrder = response.getMemberOrderDO();


        MemberOrder orderInDb = memberOrderDao.selectByTradeId(memberOrder.getUserId(), memberOrder.getTradeId());
        System.out.println(JsonUtils.toJson(orderInDb));

        //开始履约
        PerformCmd cmd = buildCmd(memberOrder);
        PerformResp resp = performBizService.perform(cmd);
        Assert.assertTrue(resp.isSuccess());
        verifyData(cmd, 1);
        checkMessageAndReset(MQTopicEnum.TRADE_EVENT, (msgs) -> Assert.assertEquals(1, msgs.size()));
        verifyTaskData(cmd, cycle3Sku.getPerformConfig().getConfigs().get(0).getCycle() - 1);

        List<MemberPerformItem> items = memberPerformItemDao.selectByTradeId(cmd.getUserId(), cmd.getTradeId());

        //触发周期履约
        OnceTaskTriggerCmd triggerCmd = new OnceTaskTriggerCmd();
        triggerCmd.setUserIds(Sets.newHashSet(submitCmd.getUserId()));
        triggerCmd.setBizType(BizTypeEnum.DEMO_MEMBER);

        checkMessageAndReset(MQTopicEnum.PRE_FINANCE_EVENT, null);
        List<OnceTask> onceTasks2 = onceTaskDao.queryTasksByUserIdAndGroupId(cmd.getUserId(), items.get(0).getSubTradeId(), TaskTypeEnum.FINANCE_EXPIRE.getCode());

        onceTaskTriggerBizService.triggerPeriodPerform(triggerCmd);
        items = memberPerformItemDao.selectByTradeId(cmd.getUserId(), cmd.getTradeId());
        List<OnceTask> onceTasks1 = onceTaskDao.queryTasksByUserIdAndGroupId(cmd.getUserId(), items.get(0).getSubTradeId(), TaskTypeEnum.FINANCE_EXPIRE.getCode());
        checkMessageAndReset(MQTopicEnum.TRADE_EVENT, (msgs) -> Assert.assertEquals(2, msgs.size()));
        checkMessageAndReset(MQTopicEnum.PRE_FINANCE_EVENT, (msgs) -> Assert.assertEquals(2, msgs.size()));

        verifyData(cmd, 3);


        //修改资产状态

        for (MemberPerformItem item : items) {
            List<AssetDO> assetDos = couponGrantFacade.assetBatchCode2Assets.get(item.getBatchCode());
            for (AssetDO assetDO : assetDos) {
                if (assetDO.getStatus() == AssetStatusEnum.UNUSE.getCode()) {
                    assetDO.setStatus(AssetStatusEnum.EXPIRE.getCode());
                }
            }
        }

        Thread.sleep(1000);

        //触发结算过期
        OnceTaskTriggerCmd financeExpireCmd = new OnceTaskTriggerCmd();
        financeExpireCmd.setBizType(BizTypeEnum.DEMO_MEMBER);
        financeExpireCmd.setTaskGroupIds(Sets.newHashSet(items.get(0).getSubTradeId()));
        financeExpireCmd.setMinTriggerStime(TimeUtil.now() - 1000000);
        financeExpireCmd.setMaxTriggerStime(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(100));
        onceTaskTriggerBizService.triggerFinanceExpire(financeExpireCmd);
        List<OnceTask> onceTasks = onceTaskDao.queryTasksByUserIdAndGroupId(cmd.getUserId(), items.get(0).getSubTradeId(), TaskTypeEnum.FINANCE_EXPIRE.getCode());
        checkMessageAndReset(MQTopicEnum.PRE_FINANCE_EVENT, (msgs) -> Assert.assertEquals(6, msgs.size()));

        //Thread.sleep(1000000);
    }

    public void checkMessageAndReset(MQTopicEnum topic) {
        checkMessageAndReset(topic, null);
    }

    public void checkMessageAndReset(MQTopicEnum topic, Consumer<List<String>> consumer) {
        if (ApplicationContextUtils.isUnitTest()) {
            if (messageQuenePublishFacade instanceof MessageQueueDebugFacade) {
                List<String> messages = ((MessageQueueDebugFacade) messageQuenePublishFacade).getMessage(topic.getName());
                ((MessageQueueDebugFacade) messageQuenePublishFacade).resetMsgs(topic.getName());
                if (consumer == null) {
                    return;
                }
                if (messages == null) {
                    throw new RuntimeException("没有获取到消息 ");
                }
                consumer.accept(messages);
            }
        }
    }

    @SneakyThrows
    @Test
    public void testDefaultMemberAndMutilPeriodCard() {
        PurchaseSubmitResponse response = submit(cycle3Sku, 1);
        MemberOrderDO memberOrder = response.getMemberOrderDO();

        MemberOrder orderInDb = memberOrderDao.selectByTradeId(memberOrder.getUserId(), memberOrder.getTradeId());
        System.out.println(JsonUtils.toJson(orderInDb));

        PerformCmd cmd = buildCmd(memberOrder);

        PerformResp resp = performBizService.perform(cmd);
        Assert.assertTrue(resp.isSuccess());
        verifyData(cmd, 1);

        verifyTaskData(cmd, cycle3Sku.getPerformConfig().getConfigs().get(0).getCycle() - 1);

        //Thread.sleep(1000000);
    }

    @SneakyThrows
    @Test
    public void testDefaultMemberAndMutilPeriodCardAndRefund() {
        PurchaseSubmitResponse response = submit(cycle3Sku, 1);
        MemberOrderDO memberOrder = response.getMemberOrderDO();

        MemberOrder orderInDb = memberOrderDao.selectByTradeId(memberOrder.getUserId(), memberOrder.getTradeId());
        System.out.println(JsonUtils.toJson(orderInDb));

        PerformCmd cmd = buildCmd(memberOrder);

        PerformResp resp = performBizService.perform(cmd);
        Assert.assertTrue(resp.isSuccess());
        verifyData(cmd, 1);

        verifyTaskData(cmd, cycle3Sku.getPerformConfig().getConfigs().get(0).getCycle() - 1);


        AfterSalePreviewCmd previewCmd = new AfterSalePreviewCmd();
        previewCmd.setUserId(cmd.getUserId());
        previewCmd.setBizType(BizTypeEnum.DEMO_MEMBER);
        previewCmd.setTradeId(cmd.getTradeId());
        previewCmd.setSource(AftersaleSourceEnum.User);
        previewCmd.setOperator(String.valueOf(cmd.getUserId()));


        AfterSalePreviewResponse respose = aftersaleBizService.preview(previewCmd);
        Assert.assertEquals(true, respose.isAftersaleEnabled());
        Assert.assertEquals(RefundTypeEnum.ALL_REFUND, respose.getRefundType());

        /*******************部分使用,结果为部分退********/
        for (Map.Entry<String, List<AssetDO>> entry : couponGrantFacade.assetBatchCode2Assets.entrySet()) {
            entry.getValue().get(0).setStatus(AssetStatusEnum.USED.getCode());
        }
        respose = aftersaleBizService.preview(previewCmd);
        Assert.assertEquals(true, respose.isAftersaleEnabled());
        Assert.assertEquals(RefundTypeEnum.PORTION_RFUND, respose.getRefundType());


        AftersaleApplyCmd applyCmd = new AftersaleApplyCmd();
        applyCmd = AftersaleConvertor.INSTANCE.toApplyCmd(previewCmd);
        applyCmd.setDigests(respose.getDigests());
        applyCmd.setReason("不想要了!");
        applyCmd.setDigestVersion(respose.getDigestVersion());
        //applyCmd.setDigestVersion(0);
        AftersaleApplyResponse aftersaleApplyResponse = aftersaleBizService.apply(applyCmd);
        //waitH2();

        verifyOrderRefund(applyCmd, false);
        Assert.assertTrue(aftersaleApplyResponse.isSuccess());


    }

    @SneakyThrows
    @Test
    public void testDefaultMemberAndBuyCount() {
        int buyCount = 3;
        PurchaseSubmitResponse response = submit(doubleRightsSku, buyCount);
        MemberOrderDO memberOrder = response.getMemberOrderDO();

        MemberOrder orderInDb = memberOrderDao.selectByTradeId(memberOrder.getUserId(), memberOrder.getTradeId());
        System.out.println(JsonUtils.toJson(orderInDb));

        PerformCmd cmd = buildCmd(memberOrder);

        PerformResp resp = performBizService.perform(cmd);
        Assert.assertTrue(resp.isSuccess());
        verifyData(cmd, buyCount);

        //Thread.sleep(1000000);
    }

    @SneakyThrows
    @Test
    public void testDefaultMember() {
        PurchaseSubmitResponse response = submit(doubleRightsSku, 1);
        MemberOrderDO memberOrder = response.getMemberOrderDO();

        MemberOrder orderInDb = memberOrderDao.selectByTradeId(memberOrder.getUserId(), memberOrder.getTradeId());
        System.out.println(JsonUtils.toJson(orderInDb));

        PerformCmd cmd = buildCmd(memberOrder);

        PerformResp resp = performBizService.perform(cmd);
        Assert.assertTrue(resp.isSuccess());
        verifyData(cmd);

        //Thread.sleep(1000000);
    }

    @SneakyThrows
    @Test
    public void testOuterSubmit() {
        OuterSubmitCmd cmd = new OuterSubmitCmd();
        cmd.setOuterId(System.currentTimeMillis() + "");
        cmd.setOuterConfigId("101");


        PurchaseSkuSubmitCmd sku = new PurchaseSkuSubmitCmd();
        sku.setSkuId(membershipSku.getSkuId());
        sku.setBuyCount(1);
        cmd.setSkus(Lists.newArrayList(sku));
        cmd.setOuterType(SubmitSourceEnum.OUTER_PURCHASE);
        CommonUserInfo userInfo = new CommonUserInfo();
        cmd.setUserInfo(userInfo);
        cmd.setUserId(userIdGenerator.incrementAndGet());
        cmd.setBizType(BizTypeEnum.DEMO_MEMBER);

        OuterSubmitResponse resp = outerSubmitBizService.submit(cmd);
        OuterSubmitResponse resp2 = outerSubmitBizService.submit(cmd);
        MemberOrder memberOrder = memberOrderDao.selectByTradeId(cmd.getUserId(), resp.getTradeId());
        Assert.assertEquals(MemberOrderStatusEnum.PERFORMED.getCode(), memberOrder.getStatus());

        OuterSubmitRecord record = outerSubmitRecordDao.selectByOutId(cmd.getUserId(), cmd.getOuterId());

        Assert.assertEquals(OuterSubmitStatusEnum.PERFORMED.getCode(), record.getStatus());
        //Thread.sleep(1000000);
    }

    @SneakyThrows
    @Test
    public void testRedeem() {
        String code = RandomUtils.nextLong() + "";
        Redeem redeem = buildRedeem(code);

        int cnt = redeemDao.insert(redeem);
        System.out.println("写入兑换码数量:" + cnt);

        OuterSubmitCmd cmd = new OuterSubmitCmd();
        cmd.setOuterId(code);
        cmd.setOuterConfigId("101");


        PurchaseSkuSubmitCmd sku = new PurchaseSkuSubmitCmd();
        sku.setSkuId(membershipSku.getSkuId());
        sku.setBuyCount(1);
        cmd.setSkus(Lists.newArrayList(sku));
        cmd.setOuterType(SubmitSourceEnum.REDEEM);
        CommonUserInfo userInfo = new CommonUserInfo();
        cmd.setUserInfo(userInfo);
        cmd.setUserId(userIdGenerator.incrementAndGet());
        cmd.setBizType(BizTypeEnum.DEMO_MEMBER);

        OuterSubmitResponse resp = outerSubmitBizService.submit(cmd);
        MemberOrder memberOrder = memberOrderDao.selectByTradeId(cmd.getUserId(), resp.getTradeId());
        Assert.assertEquals(MemberOrderStatusEnum.PERFORMED.getCode(), memberOrder.getStatus());

        OuterSubmitRecord record = outerSubmitRecordDao.selectByOutId(cmd.getUserId(), cmd.getOuterId());

        Assert.assertEquals(OuterSubmitStatusEnum.PERFORMED.getCode(), record.getStatus());

        try {
            OuterSubmitResponse resp2 = outerSubmitBizService.submit(cmd);
        } catch (Exception e) {
            if (e instanceof MemberException) {
                ResultCode resultCode = ((MemberException) e).getCode();
                Assert.assertEquals(resultCode, ResultCode.REDEEM_USE_ERROR);

            }
        }
        //Thread.sleep(1000000);
    }

    @SneakyThrows
    //@Test
    public void testDefaultMemberAndMemberShip() {
        PurchaseSubmitResponse response = submit(membershipSku, 1);
        MemberOrderDO memberOrder = response.getMemberOrderDO();

        MemberOrder orderInDb = memberOrderDao.selectByTradeId(memberOrder.getUserId(), memberOrder.getTradeId());
        System.out.println(JsonUtils.toJson(orderInDb));

        PerformCmd cmd = buildCmd(memberOrder);

        PerformResp resp = performBizService.perform(cmd);
        Assert.assertTrue(resp.isSuccess());
        verifyData(cmd);
        LambdaQueryWrapper<MemberShip> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberShip::getUserId, response.getMemberOrderDO().getUserId());
        wrapper.eq(MemberShip::getTradeId, response.getMemberOrderDO().getTradeId());
        List<MemberShip> memberShips = memberShipDao.selectList(wrapper);

        Assert.assertEquals(1, memberShips.size());
        //Thread.sleep(1000000);
    }

    @SneakyThrows
    @Test
    public void testDefaultMemberRefundAndApplyAndMemberShip() {
        PurchaseSubmitResponse submitResponse = submit(membershipSku, 1);
        MemberOrderDO memberOrder = submitResponse.getMemberOrderDO();

        MemberOrder orderInDb = memberOrderDao.selectByTradeId(memberOrder.getUserId(), memberOrder.getTradeId());
        System.out.println(JsonUtils.toJson(orderInDb));

        PerformCmd cmd = buildCmd(memberOrder);

        PerformResp resp = performBizService.perform(cmd);
        Assert.assertTrue(resp.isSuccess());
        verifyData(cmd, 1);

        AfterSalePreviewCmd previewCmd = new AfterSalePreviewCmd();
        previewCmd.setUserId(cmd.getUserId());
        previewCmd.setBizType(BizTypeEnum.DEMO_MEMBER);
        previewCmd.setTradeId(cmd.getTradeId());
        previewCmd.setSource(AftersaleSourceEnum.User);
        previewCmd.setOperator(String.valueOf(cmd.getUserId()));

        AfterSalePreviewResponse response = aftersaleBizService.preview(previewCmd);

        Assert.assertEquals(true, response.isAftersaleEnabled());
        Assert.assertEquals(RefundTypeEnum.ALL_REFUND, response.getRefundType());

        AftersaleApplyCmd applyCmd = AftersaleConvertor.INSTANCE.toAftersaleApplyCmd(previewCmd);
        applyCmd.setDigests(response.getDigests());
        applyCmd.setDigestVersion(response.getDigestVersion());

        AftersaleApplyResponse applyResponse = aftersaleBizService.apply(applyCmd);
        waitH2();

        Assert.assertTrue(applyResponse.isSuccess());


        verifyOrderRefund(applyCmd, true);
    }

    @SneakyThrows
    @Test
    public void testDefaultMemberRefundAndApply() {
        PurchaseSubmitResponse submitResponse = submit(doubleRightsSku, 3);
        MemberOrderDO memberOrder = submitResponse.getMemberOrderDO();

        MemberOrder orderInDb = memberOrderDao.selectByTradeId(memberOrder.getUserId(), memberOrder.getTradeId());
        System.out.println(JsonUtils.toJson(orderInDb));

        PerformCmd cmd = buildCmd(memberOrder);

        PerformResp resp = performBizService.perform(cmd);
        Assert.assertTrue(resp.isSuccess());
        verifyData(cmd, 3);

        AfterSalePreviewCmd previewCmd = new AfterSalePreviewCmd();
        previewCmd.setUserId(cmd.getUserId());
        previewCmd.setBizType(BizTypeEnum.DEMO_MEMBER);
        previewCmd.setTradeId(cmd.getTradeId());
        previewCmd.setSource(AftersaleSourceEnum.User);
        previewCmd.setOperator(String.valueOf(cmd.getUserId()));

        AfterSalePreviewResponse response = aftersaleBizService.preview(previewCmd);

        Assert.assertEquals(true, response.isAftersaleEnabled());
        Assert.assertEquals(RefundTypeEnum.ALL_REFUND, response.getRefundType());

        AftersaleApplyCmd applyCmd = AftersaleConvertor.INSTANCE.toAftersaleApplyCmd(previewCmd);
        applyCmd.setDigests(response.getDigests());
        applyCmd.setDigestVersion(response.getDigestVersion());

        AftersaleApplyResponse applyResponse = aftersaleBizService.apply(applyCmd);
        waitH2();

        Assert.assertTrue(applyResponse.isSuccess());


        verifyOrderRefund(applyCmd, true);
    }

    private void verifyOrderRefund(AftersaleApplyCmd applyCmd, boolean completeRefund) {
        List<AftersaleOrder> orders = aftersaleOrderDao.queryByTradeId(applyCmd.getUserId(), applyCmd.getTradeId());
        Assert.assertEquals(1, orders.size());

        for (AftersaleOrder order : orders) {
            Assert.assertEquals(AftersaleOrderStatusEnum.AFTERSALE_SUCCESS.getCode(), order.getStatus());
        }

        List<MemberSubOrder> hisAfterApply = memberSubOrderDao.selectByTradeId(applyCmd.getUserId(), applyCmd.getTradeId());
        List<MemberPerformItem> itemsAfterApply = memberPerformItemDao.selectByTradeId(applyCmd.getUserId(), applyCmd.getTradeId());
        for (MemberSubOrder his : hisAfterApply) {
            Assert.assertEquals(SubOrderPerformStatusEnum.getReversedStatus(completeRefund),
                    his.getPerformStatus());
            if (completeRefund) {
                Assert.assertEquals(SubOrderStatusEnum.REFUNDED.getCode(), his.getStatus());
            } else {
                Assert.assertEquals(SubOrderStatusEnum.PERFORMED.getCode(), his.getStatus());
            }
        }
        for (MemberPerformItem item : itemsAfterApply) {
            Assert.assertEquals(PerformItemStatusEnum.getReversedStatus(completeRefund),
                    item.getStatus());
            for (Map.Entry<String, List<AssetDO>> entry : couponGrantFacade.assetBatchCode2Assets.entrySet()) {
                if (StringUtils.equals(item.getBatchCode(), entry.getKey())) {
                    boolean hasReverse = entry.getValue().stream().anyMatch(v ->
                            v.getStatus() == AssetStatusEnum.FREEZE.getCode());
                    Assert.assertTrue(hasReverse);
                }
            }
        }
        for (MemberSubOrder memberSubOrder : hisAfterApply) {
            List<OnceTask> tasks =
                    onceTaskDao.queryTasksByUserIdAndGroupId(memberSubOrder.getUserId(),
                            String.valueOf(memberSubOrder.getSubTradeId()),
                            TaskTypeEnum.PERIOD_PERFORM.getCode());

            for (OnceTask task : tasks) {
                if (task.getStatus() == OnceTaskStatusEnum.CANCEL.getCode() ||
                        task.getStatus() == OnceTaskStatusEnum.SUCCESS.getCode()) {

                } else {
                    Assert.fail("任务状态异常:" + task.getStatus());
                }
            }
        }

    }

    @SneakyThrows
    @Test
    public void testDefaultMemberRefundAndApplyAndInventoryAndNewMemberAndQuota() {
        PurchaseSubmitResponse submitResponse = submit(inventoryEnabledSku, 3);
        MemberOrderDO memberOrder = submitResponse.getMemberOrderDO();

        MemberOrder orderInDb = memberOrderDao.selectByTradeId(memberOrder.getUserId(), memberOrder.getTradeId());
        System.out.println(JsonUtils.toJson(orderInDb));

        PerformCmd cmd = buildCmd(memberOrder);

        PerformResp resp = performBizService.perform(cmd);
        Assert.assertTrue(resp.isSuccess());
        verifyData(cmd, 3);

        AfterSalePreviewCmd previewCmd = new AfterSalePreviewCmd();
        previewCmd.setUserId(cmd.getUserId());
        previewCmd.setBizType(BizTypeEnum.DEMO_MEMBER);
        previewCmd.setTradeId(cmd.getTradeId());
        previewCmd.setSource(AftersaleSourceEnum.User);
        previewCmd.setOperator(String.valueOf(cmd.getUserId()));

        AfterSalePreviewResponse response = aftersaleBizService.preview(previewCmd);

        Assert.assertEquals(true, response.isAftersaleEnabled());
        Assert.assertEquals(RefundTypeEnum.ALL_REFUND, response.getRefundType());

        AftersaleApplyCmd applyCmd = AftersaleConvertor.INSTANCE.toAftersaleApplyCmd(previewCmd);
        applyCmd.setDigests(response.getDigests());
        applyCmd.setDigestVersion(response.getDigestVersion());

        AftersaleApplyResponse applyResponse = aftersaleBizService.apply(applyCmd);
        waitH2();

        Assert.assertTrue(applyResponse.isSuccess());

        verifyOrderRefund(applyCmd, true);
    }

    @SneakyThrows
    @Test
    public void testDefaultMemberRefund() {
        PurchaseSubmitResponse submitResponse = submit(doubleRightsSku, 3);
        MemberOrderDO memberOrder = submitResponse.getMemberOrderDO();

        MemberOrder orderInDb = memberOrderDao.selectByTradeId(memberOrder.getUserId(), memberOrder.getTradeId());
        System.out.println(JsonUtils.toJson(orderInDb));

        PerformCmd cmd = buildCmd(memberOrder);

        PerformResp resp = performBizService.perform(cmd);
        Assert.assertTrue(resp.isSuccess());
        verifyData(cmd, 3);

        AfterSalePreviewCmd previewCmd = new AfterSalePreviewCmd();
        previewCmd.setUserId(cmd.getUserId());
        previewCmd.setBizType(BizTypeEnum.DEMO_MEMBER);
        previewCmd.setTradeId(cmd.getTradeId());
        previewCmd.setSource(AftersaleSourceEnum.User);
        previewCmd.setOperator(String.valueOf(cmd.getUserId()));


        AfterSalePreviewResponse respose = aftersaleBizService.preview(previewCmd);
        Assert.assertEquals(true, respose.isAftersaleEnabled());
        Assert.assertEquals(RefundTypeEnum.ALL_REFUND, respose.getRefundType());

        /*******************部分使用,结果为部分退********/
        for (Map.Entry<String, List<AssetDO>> entry : couponGrantFacade.assetBatchCode2Assets.entrySet()) {
            entry.getValue().get(0).setStatus(AssetStatusEnum.USED.getCode());
        }
        respose = aftersaleBizService.preview(previewCmd);
        Assert.assertEquals(true, respose.isAftersaleEnabled());
        Assert.assertEquals(RefundTypeEnum.PORTION_RFUND, respose.getRefundType());


        AftersaleApplyCmd applyCmd = new AftersaleApplyCmd();
        applyCmd = AftersaleConvertor.INSTANCE.toApplyCmd(previewCmd);
        applyCmd.setDigests(respose.getDigests());
        applyCmd.setReason("不想要了!");
        applyCmd.setDigestVersion(respose.getDigestVersion());
        //applyCmd.setDigestVersion(0);
        AftersaleApplyResponse aftersaleApplyResponse = aftersaleBizService.apply(applyCmd);
        //waitH2();

        verifyOrderRefund(applyCmd, false);
        Assert.assertTrue(aftersaleApplyResponse.isSuccess());

        /*   *//*******************已用尽,结果为不可退********//*

        for (Map.Entry<String, List<AssetDO>> entry : couponGrantFacade.assetBatchCode2Assets.entrySet()) {
            for (AssetDO assetDO : entry.getValue()) {
                assetDO.setStatus(AssetStatusEnum.USED.getCode());
            }
        }
        respose = aftersaleBizService.preview(previewCmd);
        Assert.assertEquals(false, respose.isAftersaleEnabled());
        Assert.assertEquals(AftersaleUnableCode.USE_OUT_ERROR.getCode(), respose.getUnableCode());*/
        //Thread.sleep(10000000);
    }

    private void verifyData(PerformCmd cmd) {
        verifyData(cmd, 1);
    }

    private void verifyData(PerformCmd cmd, int buyCount) {
        List<MemberSubOrder> subOrders = memberSubOrderDao.selectByTradeId(cmd.getUserId(), cmd.getTradeId());
        for (MemberSubOrder memberSubOrder : subOrders) {
            Assert.assertEquals(SubOrderPerformStatusEnum.PERFORM_SUCCESS.getCode(), memberSubOrder.getPerformStatus());
        }
        Assert.assertEquals(1, subOrders.size());
        List<MemberPerformItem> items = memberPerformItemDao.selectByTradeId(cmd.getUserId(), cmd.getTradeId());

        for (MemberPerformItem item : items) {
            Assert.assertEquals(PerformItemStatusEnum.PERFORM_SUCCESS.getCode(), item.getStatus());
        }
        Assert.assertTrue(items.size() >= buyCount * 2);

        MemberOrder orderFromDb = memberOrderDao.selectByTradeId(cmd.getUserId(), cmd.getTradeId());
        Assert.assertEquals(MemberOrderStatusEnum.PERFORMED.getCode(), orderFromDb.getStatus());

        System.out.println(JsonUtils.toJson(subOrders));
    }

    private void verifyTaskData(PerformCmd cmd, int taskSize) {
        List<MemberSubOrder> subOrders = memberSubOrderDao.selectByTradeId(cmd.getUserId(), cmd.getTradeId());

        for (MemberSubOrder subOrder : subOrders) {
            List<OnceTask> tasks = onceTaskDao.queryTasksByUserIdAndGroupId(cmd.getUserId(),
                    String.valueOf(subOrder.getSubTradeId()), TaskTypeEnum.PERIOD_PERFORM.getCode());
            Assert.assertEquals(taskSize, tasks.size());

            for (OnceTask task : tasks) {
                Assert.assertNotNull(task.getTaskGroupId());
            }
        }
    }

    @SneakyThrows
    private PerformCmd buildCmd(MemberOrderDO memberOrder) {
        PerformCmd cmd = new PerformCmd();
        cmd.setBizType(BizTypeEnum.DEMO_MEMBER);
        cmd.setUserId(memberOrder.getUserId());
        cmd.setTradeId(memberOrder.getTradeId());
        return cmd;
    }

    private MemberOrder buildMemberOrder() {
        return buildMemberOrder(1, 1);
    }

    @SneakyThrows
    private MemberOrder buildMemberOrder(int buyCount, int cycle) {
        MemberOrder memberOrder = new MemberOrder();
        //memberOrder.setUserId(userIdGenerator.incrementAndGet());
        memberOrder.setUserId(DEFAULT_USER_ID);
        memberOrder.setRelatedOrderId(orderIdGenerator.incrementAndGet() + "");
        memberOrder.setRelatedOrderSystemType(1);
        memberOrder.setOriginPriceFen(3000);
        memberOrder.setActPriceFen(699);
        memberOrder.setBizType(1);
        memberOrder.setCtime(TimeUtil.now());
        memberOrder.setExtra("{}");
        memberOrder.setStatus(MemberOrderStatusEnum.PAYED.getCode());
        memberOrder.setTradeId(String.format("%s_%s", memberOrder.getRelatedOrderSystemType(), memberOrder.getRelatedOrderId()));

        List<SkuInfoDO> skuInfoDOS = Lists.newArrayList();


        CommonUserInfo userInfo = new CommonUserInfo();
        userInfo.setUuid(RandomStringUtils.randomAlphabetic(12));
        userInfo.setPhone(RandomStringUtils.randomNumeric(11));
        userInfo.setMaskedPhone(EncrptUtils.generatePhoneMask(userInfo.getPhone()));


        String key = EncrptUtils.generateAESKey();

        userInfo.setKey(key);
        userInfo.setEncryptedPhone(EncrptUtils.encryptAES(userInfo.getPhone(), key));

        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setActualLatitude("8493458355");
        locationInfo.setActualLongitude("48934834");
        locationInfo.setActualSecondCityId("110100");
        locationInfo.setActualThirdCityId("4384394");

        MemberOrderExtraInfo extraInfo = new MemberOrderExtraInfo();
        extraInfo.setUserInfo(userInfo);
        extraInfo.setLocationInfo(locationInfo);

        memberOrder.setExtra(JsonUtils.toJson(extraInfo));

        System.out.println("解密后的电话号码： " + EncrptUtils.decrypt(userInfo.getEncryptedPhone(), userInfo.getKey()));

        //memberOrder.setSkuDetails(JsonUtils.toJson(skuInfoDOS));
        return memberOrder;
    }

    @Test
    @JustUnitTest
    public void test() {
        if (!ApplicationContextUtils.isUnitTest()) {
            CommonLog.info("当前 Profile:{} 不执行本单测",
                    JsonUtils.toJson(ApplicationContextUtils.getContext().getEnvironment().getActiveProfiles()));
            return;
        }

        memberSubOrderDao.selectByUserId(1000);
        MemberSubOrder his = new MemberSubOrder();
        his.setBizType(1);
        his.setUserId(1000);

        TimeRange timeRange = PeriodUtils.buildTimeRangeFromBaseTime(2);


        his.setEtime(timeRange.getEtime());
        his.setStime(timeRange.getStime());
        his.setSkuId(10);
        his.setRelatedOrderId("1001");
        his.setRelatedOrderSystemType(1);
        his.setBuyCount(1);
        his.setSubTradeId(RandomUtils.nextLong());
        his.setOriginPriceFen(2323);
        his.setSalePriceFen(32343);
        his.setActPriceFen(4343);
        his.setTradeId(RandomStringUtils.random(12));


        his.setExtra("{}");
        his.setCtime(TimeUtil.now());
        his.setUtime(TimeUtil.now());

        int count = memberSubOrderDao.insert(his);
        System.out.println("插入数量" + count);
        List<MemberSubOrder> hisLists = memberSubOrderDao.selectByUserId(1000);
        System.out.println(hisLists);
    }

    @Test
    public void testJson() {
        MemberSubOrder his = new MemberSubOrder();
        his.setBizType(1);
        his.setUserId(1000);

        TimeRange timeRange = PeriodUtils.buildTimeRangeFromBaseTime(2);


        his.setEtime(timeRange.getEtime());
        his.setStime(timeRange.getStime());
        his.setSkuId(10);
        his.setRelatedOrderId("1001");
        his.setRelatedOrderSystemType(1);
        his.setTradeId("1_1001");
        his.setExtra("{}");
        his.setCtime(TimeUtil.now());
        his.setUtime(TimeUtil.now());

        String json = JsonUtils.toJson(his);
        System.out.println(json);
        MemberSubOrder performHis = JsonUtils.fromJson(json, MemberSubOrder.class);
        System.out.println(performHis);
    }

    @Test
    public void testItem() {
        if (!ApplicationContextUtils.isUnitTest()) {
            CommonLog.info("当前 Profile:{} 不执行本单测",
                    JsonUtils.toJson(ApplicationContextUtils.getContext().getEnvironment().getActiveProfiles()));
            return;
        }
        int cnt = 3;
        List<MemberPerformItem> items = Lists.newArrayList();
        for (int i = cnt; i > 0; i--) {
            MemberPerformItem item = new MemberPerformItem();
            item.setTotalCount(4);
            item.setBatchCode("2323");
            item.setBizType(1);
            item.setBuyIndex(1);
            item.setCtime(TimeUtil.now());
            item.setCycle(1);
            item.setPhase(1);
            item.setItemToken(RandomStringUtils.random(10));
            item.setRightId(1);
            item.setRightType(1);
            item.setSkuId(1);
            item.setGrantType(1);
            item.setStatus(1);
            item.setTradeId("gjeigejoig" + i);
            item.setStime(TimeUtil.now());
            item.setEtime(TimeUtil.now());
            item.setUserId(1);
            item.setUtime(TimeUtil.now());
            item.setExtra("{}");
            items.add(item);
        }

        int num = memberPerformItemDao.insertIgnoreBatch(items);
        System.out.println(num);

        List<MemberPerformItem> itemsDb = memberPerformItemDao.selectByMap(ImmutableMap.of("user_id", 1));
        System.out.println(JsonUtils.toJson(itemsDb));
    }

    /*@SpyBean(name = "couponGrantFacade", value = MockCouponGrantFacade.class)
    private CouponGrantFacade mockCouponGrantFacade;*/
}