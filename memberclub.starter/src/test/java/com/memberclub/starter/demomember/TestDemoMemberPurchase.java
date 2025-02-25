/**
 * @(#)TestDemoMemberPurchase.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.demomember;

import com.google.common.collect.ImmutableList;
import com.memberclub.common.util.JsonUtils;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.perform.common.PeriodTypeEnum;
import com.memberclub.domain.context.purchase.PurchaseSkuSubmitCmd;
import com.memberclub.domain.context.purchase.PurchaseSubmitCmd;
import com.memberclub.domain.context.purchase.PurchaseSubmitResponse;
import com.memberclub.domain.context.purchase.cancel.PurchaseCancelCmd;
import com.memberclub.domain.context.purchase.common.MemberOrderStatusEnum;
import com.memberclub.domain.context.purchase.common.PurchaseSourceEnum;
import com.memberclub.domain.dataobject.CommonUserInfo;
import com.memberclub.domain.dataobject.aftersale.ClientInfo;
import com.memberclub.domain.dataobject.inventory.InventoryCacheDO;
import com.memberclub.domain.dataobject.order.LocationInfo;
import com.memberclub.domain.dataobject.sku.*;
import com.memberclub.domain.dataobject.sku.restrict.RestrictItemType;
import com.memberclub.domain.dataobject.sku.restrict.RestrictPeriodType;
import com.memberclub.domain.dataobject.sku.restrict.SkuRestrictInfo;
import com.memberclub.domain.dataobject.sku.restrict.SkuRestrictItem;
import com.memberclub.domain.dataobject.sku.rights.RightFinanceInfo;
import com.memberclub.domain.dataobject.sku.rights.RightViewInfo;
import com.memberclub.domain.entity.inventory.Inventory;
import com.memberclub.domain.entity.inventory.InventoryTargetTypeEnum;
import com.memberclub.domain.entity.trade.MemberOrder;
import com.memberclub.domain.entity.trade.MemberSubOrder;
import com.memberclub.domain.exception.MemberException;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.cache.CacheEnum;
import com.memberclub.infrastructure.cache.CacheService;
import com.memberclub.infrastructure.order.facade.MockCommonOrderFacadeSPI;
import com.memberclub.sdk.inventory.service.InventoryDomainService;
import com.memberclub.sdk.purchase.service.biz.PurchaseBizService;
import com.memberclub.sdk.sku.service.SkuDomainService;
import com.memberclub.starter.mock.MockBaseTest;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * author: 掘金五阳
 */
public class TestDemoMemberPurchase extends MockBaseTest {


    public static SkuInfoDO doubleRightsSku = null;

    public static SkuInfoDO membershipSku = null;

    public static SkuInfoDO cycle3Sku = null;

    public static SkuInfoDO inventoryEnabledSku = null;

    @Autowired
    public PurchaseBizService purchaseBizService;


    @Autowired
    private SkuDomainService skuDomainService;

    private static boolean init = false;

    public static AtomicLong skuIdGenerator = new AtomicLong(200300);

    private SkuInfoDO buildMemberShipSku() {
        membershipSku = buildDoubleRightsSku(1);

        SkuPerformItemConfigDO shipConfig = new SkuPerformItemConfigDO();
        membershipSku.getPerformConfig().getConfigs().add(shipConfig);


        shipConfig.setAssetCount(0);
        shipConfig.setBizType(1);
        shipConfig.setCycle(1);
        shipConfig.setPeriodType(PeriodTypeEnum.FIX_DAY.getCode());
        shipConfig.setRightId(32425);
        shipConfig.setPeriodCount(31);
        shipConfig.setRightType(3);
        shipConfig.setProviderId("3");
        RightViewInfo rightViewInfo = new RightViewInfo();
        rightViewInfo.setDisplayName("会员身份");
        shipConfig.setViewInfo(rightViewInfo);


        RightFinanceInfo rightFinanceInfo2 = new RightFinanceInfo();
        rightFinanceInfo2.setFinanceable(false);
        shipConfig.setSettleInfo(rightFinanceInfo2);

        return membershipSku;
    }

    private SkuInfoDO buildInventorySku() {
        inventoryEnabledSku = buildDoubleRightsSku(1);

        SkuInventoryInfo inventoryInfo = new SkuInventoryInfo();
        inventoryInfo.setEnable(true);
        inventoryInfo.setTotal(100L);
        inventoryInfo.setType(InventoryTypeEnum.TOTAL.getCode());
        inventoryEnabledSku.setInventoryInfo(inventoryInfo);

        SkuRestrictInfo skuRestrictInfo = new SkuRestrictInfo();
        skuRestrictInfo.setEnable(true);
        List<SkuRestrictItem> skuRestrictItems = Lists.newArrayList();
        skuRestrictInfo.setRestrictItems(skuRestrictItems);

        SkuRestrictItem item = new SkuRestrictItem();
        item.setTotal(1000000L);
        item.setPeriodType(RestrictPeriodType.TOTAL);
        item.setPeriodCount(31);
        item.setItemType(RestrictItemType.TOTAL);
        item.setUserTypes(Lists.newArrayList(UserTypeEnum.USERID));
        skuRestrictItems.add(item);


        SkuRestrictItem item2 = new SkuRestrictItem();
        item2.setTotal(1000000L);
        item2.setPeriodCount(31);
        item2.setPeriodType(RestrictPeriodType.TOTAL);
        item2.setItemType(RestrictItemType.SKU);
        item2.setUserTypes(Lists.newArrayList(UserTypeEnum.USERID));
        skuRestrictItems.add(item2);

        inventoryEnabledSku.setRestrictInfo(skuRestrictInfo);

        SkuNewMemberInfo skuNewMemberInfo = new SkuNewMemberInfo();
        skuNewMemberInfo.setNewMemberMarkEnable(true);
        skuNewMemberInfo.setUserTypes(ImmutableList.of(UserTypeEnum.USERID));
        inventoryEnabledSku.getExtra().setSkuNewMemberInfo(skuNewMemberInfo);
        return inventoryEnabledSku;
    }

    @Test
    @SneakyThrows
    public void testSubmit() {
        PurchaseSubmitCmd cmd = buildPurchaseSubmitCmd(doubleRightsSku.getSkuId(), 2);
        PurchaseSubmitResponse response = purchaseBizService.submit(cmd);

        Assert.assertEquals(true, response.isSuccess());

        List<MemberSubOrder> subOrders = memberSubOrderDao.selectByTradeId(cmd.getUserId(), response.getMemberOrderDO().getTradeId());

        MemberOrder order = memberOrderDao.selectByTradeId(cmd.getUserId(), response.getMemberOrderDO().getTradeId());
        Assert.assertEquals(MemberOrderStatusEnum.SUBMITED.getCode(), order.getStatus());

        for (MemberSubOrder subOrder : subOrders) {
            //Assert.assertEquals(SubOrderStatusEnum.SUBMITED.getCode(), subOrder.getStatus());
        }
        releaseLock(response.getLockValue());
        waitH2();
    }


    @Test
    @SneakyThrows
    public void testSubmitInventoryLoss() {
        PurchaseSubmitCmd cmd = buildPurchaseSubmitCmd(inventoryEnabledSku.getSkuId(), 101);
        try {
            PurchaseSubmitResponse response = purchaseBizService.submit(cmd);
            releaseLock(response.getLockValue());
            Assert.assertEquals(false, response.isSuccess());
        } catch (MemberException e) {
            if (e instanceof MemberException) {
                if (((MemberException) e).getCode() == ResultCode.INVENTORY_LACKING) {
                    return;
                }
            }
            Assert.fail("库存扣减校验失败");
        }
    }

    @SpyBean
    private MockCommonOrderFacadeSPI mockCommonOrderFacadeSPI;

    @Autowired
    private InventoryDomainService inventoryDomainService;

    @Test
    @SneakyThrows
    public void testSubmitInventoryRollback() {
        PurchaseSubmitCmd cmd = buildPurchaseSubmitCmd(inventoryEnabledSku.getSkuId(), 101);

        List<Inventory> inventories = inventoryDomainService.queryInventorys(inventoryEnabledSku.getSkuId());
        Assert.assertEquals(1, inventories.size());
        Inventory pre = inventories.get(0);
        try {

            Mockito.doThrow(new RuntimeException("mock submit order error"))
                    .when(mockCommonOrderFacadeSPI).submit(Mockito.any());

            PurchaseSubmitResponse response = purchaseBizService.submit(cmd);
            releaseLock(response.getLockValue());
            Assert.assertEquals(false, response.isSuccess());
        } catch (MemberException e) {
            if (e.getCode() == ResultCode.INVENTORY_LACKING) {
                inventories = inventoryDomainService.queryInventorys(inventoryEnabledSku.getSkuId());
                Assert.assertEquals(pre.getSaleCount(), inventories.get(0).getSaleCount());
                Assert.assertEquals(pre.getVersion(), inventories.get(0).getVersion());
                InventoryCacheDO cache = cacheService.get(CacheEnum.inventory, Inventory.buildInventoryKey(
                        InventoryTargetTypeEnum.SKU.getCode(), inventoryEnabledSku.getSkuId(), "total"));
                System.out.println("从缓存查出的库存:" + JsonUtils.toJson(cache));
                return;
            }
            Assert.fail("失败");
        } finally {
            Mockito.reset(mockCommonOrderFacadeSPI);
        }
    }

    @Test
    @SneakyThrows
    public void testSubmitInventory() {
        PurchaseSubmitCmd cmd = buildPurchaseSubmitCmd(inventoryEnabledSku.getSkuId(), 3);
        PurchaseSubmitResponse response = purchaseBizService.submit(cmd);

        Assert.assertEquals(true, response.isSuccess());

        List<MemberSubOrder> subOrders = memberSubOrderDao.selectByTradeId(cmd.getUserId(), response.getMemberOrderDO().getTradeId());

        MemberOrder order = memberOrderDao.selectByTradeId(cmd.getUserId(), response.getMemberOrderDO().getTradeId());
        Assert.assertEquals(MemberOrderStatusEnum.SUBMITED.getCode(), order.getStatus());

        for (MemberSubOrder subOrder : subOrders) {
            //Assert.assertEquals(SubOrderStatusEnum.SUBMITED.getCode(), subOrder.getStatus());
        }

        PurchaseCancelCmd cancelCmd = new PurchaseCancelCmd();
        cancelCmd.setBizType(cmd.getBizType());
        cancelCmd.setUserId(cmd.getUserId());
        cancelCmd.setTradeId(response.getMemberOrderDO().getTradeId());
        purchaseBizService.cancel(cancelCmd);

        //releaseLock(response.getLockValue());
        waitH2();
    }

    public PurchaseSubmitResponse submit(SkuInfoDO skuInfoDO, int buyCount) {
        PurchaseSubmitCmd cmd = buildPurchaseSubmitCmd(skuInfoDO.getSkuId(), buyCount);
        PurchaseSubmitResponse response = purchaseBizService.submit(cmd);
        return response;
    }


    public static PurchaseSubmitCmd buildPurchaseSubmitCmd(long skuId, int buyCount) {
        PurchaseSubmitCmd cmd = new PurchaseSubmitCmd();
        LocationInfo locationInfo = new LocationInfo();
        ClientInfo clientInfo = new ClientInfo();
        CommonUserInfo userInfo = new CommonUserInfo();

        locationInfo.setActualSecondCityId("110100");
        clientInfo.setClientCode(1);
        clientInfo.setClientName("ios");

        userInfo.setIp("127.0.0.1");

        cmd.setClientInfo(clientInfo);
        cmd.setUserInfo(userInfo);
        cmd.setLocationInfo(locationInfo);

        //cmd.setUserId(userIdGenerator.incrementAndGet());
        cmd.setUserId(DEFAULT_USER_ID);
        cmd.setBizType(BizTypeEnum.DEMO_MEMBER);

        PurchaseSkuSubmitCmd sku = new PurchaseSkuSubmitCmd();
        sku.setSkuId(skuId);
        sku.setBuyCount(buyCount);
        cmd.setSkus(Lists.newArrayList(sku));

        cmd.setSource(PurchaseSourceEnum.HOMEPAGE);
        cmd.setSubmitToken(RandomStringUtils.randomAscii(10));
        return cmd;
    }

    @Autowired
    private CacheService cacheService;

    @Before
    public void init() {
        if (init) {
            return;
        }
        init = true;
        doubleRightsSku = buildDoubleRightsSku(1);
        mockSkuBizService.addSku(doubleRightsSku.getSkuId(), doubleRightsSku);

        cycle3Sku = buildDoubleRightsSku(3);
        mockSkuBizService.addSku(cycle3Sku.getSkuId(), cycle3Sku);


        inventoryEnabledSku = buildInventorySku();
        mockSkuBizService.addSkuAndCreateInventory(inventoryEnabledSku.getSkuId(), inventoryEnabledSku);

        membershipSku = buildMemberShipSku();
        mockSkuBizService.addSku(membershipSku.getSkuId(), membershipSku);


    }

    public static SkuInfoDO buildDoubleRightsSku(int cycle) {
        SkuInfoDO skuInfoDO = new SkuInfoDO();

        skuInfoDO.setSkuId(skuIdGenerator.incrementAndGet());
        skuInfoDO.setBizType(BizTypeEnum.DEMO_MEMBER.getCode());
        skuInfoDO.setCtime(TimeUtil.now());
        skuInfoDO.setUtime(TimeUtil.now());

        SkuSaleInfo skuSaleInfo = new SkuSaleInfo();
        skuSaleInfo.setOriginPriceFen(3000);
        skuSaleInfo.setSalePriceFen(699);

        skuInfoDO.setSaleInfo(skuSaleInfo);

        SkuFinanceInfo settleInfo = new SkuFinanceInfo();
        settleInfo.setContractorId("438098434");
        settleInfo.setSettlePriceFen(300);
        settleInfo.setFinanceProductType(1);
        settleInfo.setPeriodCycle(cycle);

        skuInfoDO.setFinanceInfo(settleInfo);

        SkuViewInfo viewInfo = new SkuViewInfo();
        viewInfo.setDisplayDesc("大额红包");
        viewInfo.setDisplayName("大额红包");
        viewInfo.setInternalDesc("大额红包 5 元");
        viewInfo.setInternalName("大额红包 5 元");
        skuInfoDO.setViewInfo(viewInfo);


        SkuPerformConfigDO skuPerformConfigDO = new SkuPerformConfigDO();
        skuInfoDO.setPerformConfig(skuPerformConfigDO);


        SkuPerformItemConfigDO skuPerformItemConfigDO = new SkuPerformItemConfigDO();
        skuPerformItemConfigDO.setAssetCount(4);
        skuPerformItemConfigDO.setBizType(1);
        skuPerformItemConfigDO.setCycle(cycle);
        skuPerformItemConfigDO.setPeriodType(PeriodTypeEnum.FIX_DAY.getCode());
        skuPerformItemConfigDO.setRightId(32424);
        skuPerformItemConfigDO.setPeriodCount(31);
        skuPerformItemConfigDO.setRightType(1);
        skuPerformItemConfigDO.setProviderId("1");
        RightViewInfo rightViewInfo = new RightViewInfo();
        rightViewInfo.setDisplayName("会员立减券权益");

        skuPerformItemConfigDO.setViewInfo(rightViewInfo);

        RightFinanceInfo rightFinanceInfo = new RightFinanceInfo();
        rightFinanceInfo.setContractorId("438098434");
        rightFinanceInfo.setSettlePriceFen(233);
        rightFinanceInfo.setFinanceable(true);
        rightFinanceInfo.setFinanceAssetType(1);
        skuPerformItemConfigDO.setSettleInfo(rightFinanceInfo);

        SkuPerformItemConfigDO skuPerformItemConfigDO2 = new SkuPerformItemConfigDO();
        skuPerformItemConfigDO2.setAssetCount(4);
        skuPerformItemConfigDO2.setBizType(1);
        skuPerformItemConfigDO2.setCycle(cycle);
        skuPerformItemConfigDO2.setPeriodType(PeriodTypeEnum.FIX_DAY.getCode());
        skuPerformItemConfigDO2.setRightId(32423);
        skuPerformItemConfigDO2.setPeriodCount(31);
        skuPerformItemConfigDO2.setRightType(2);
        skuPerformItemConfigDO2.setProviderId("1");
        rightViewInfo = new RightViewInfo();
        rightViewInfo.setDisplayName("会员折扣券权益");
        skuPerformItemConfigDO2.setViewInfo(rightViewInfo);


        RightFinanceInfo rightFinanceInfo2 = new RightFinanceInfo();
        rightFinanceInfo2.setContractorId("438098434");
        rightFinanceInfo2.setSettlePriceFen(233);
        rightFinanceInfo2.setFinanceable(true);
        rightFinanceInfo2.setFinanceAssetType(2);
        skuPerformItemConfigDO2.setSettleInfo(rightFinanceInfo2);

        skuPerformConfigDO.setConfigs(Lists.newArrayList(skuPerformItemConfigDO, skuPerformItemConfigDO2));
        skuInfoDO.setPerformConfig(skuPerformConfigDO);

        skuInfoDO.setExtra(new SkuExtra());
        return skuInfoDO;
    }
}