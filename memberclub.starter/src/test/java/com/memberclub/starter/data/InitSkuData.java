package com.memberclub.starter.data;

import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.perform.common.PeriodTypeEnum;
import com.memberclub.domain.context.perform.common.RightTypeEnum;
import com.memberclub.domain.dataobject.sku.*;
import com.memberclub.domain.dataobject.sku.restrict.RestrictItemType;
import com.memberclub.domain.dataobject.sku.restrict.RestrictPeriodType;
import com.memberclub.domain.dataobject.sku.restrict.SkuRestrictInfo;
import com.memberclub.domain.dataobject.sku.restrict.SkuRestrictItem;
import com.memberclub.domain.dataobject.sku.rights.RightFinanceInfo;
import com.memberclub.domain.dataobject.sku.rights.RightViewInfo;
import com.memberclub.starter.mock.MockBaseTest;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class InitSkuData extends MockBaseTest {
    public static AtomicLong skuIdGenerator = new AtomicLong(200400);

    //初始化电商季卡
    @Test
    public void testInitSkuData() {
        //模仿京东plus 会员季卡
        SkuInfoDO skuInfoDO = InitMallMemberSKU.buildMallMemberSKu();
        mockSkuBizService.addSku(skuInfoDO.getSkuId(), skuInfoDO);

        //模仿京东plus 会员月卡
        SkuInfoDO monthSkuInfoDO = InitMallMemberSKU.buildMallMemberSKuMonth();
        mockSkuBizService.addSku(monthSkuInfoDO.getSkuId(), monthSkuInfoDO);

        //模仿抖音单券，单权益类商品
        SkuInfoDO douyinSingleRightSku = InitDouyinCouponPackageSku.buildSingleRightSku();
        mockSkuBizService.addSku(douyinSingleRightSku.getSkuId(), douyinSingleRightSku);

        //模仿抖音单券，组合券，双权益类商品
        SkuInfoDO douyinDoubleRightSku = InitDouyinCouponPackageSku.buildDoubleRightsSku();
        mockSkuBizService.addSku(douyinDoubleRightSku.getSkuId(), douyinDoubleRightSku);


    }

    public static class InitDouyinCouponPackageSku {
        public static SkuInfoDO buildDoubleRightsSku() {
            SkuInfoDO skuInfoDO = new SkuInfoDO();

            skuInfoDO.setSkuId(skuIdGenerator.incrementAndGet());
            skuInfoDO.setBizType(BizTypeEnum.DOUYIN_COUPON_PACKAGE.getCode());
            skuInfoDO.setCtime(TimeUtil.now());
            skuInfoDO.setUtime(TimeUtil.now());

            SkuSaleInfo skuSaleInfo = new SkuSaleInfo();
            skuSaleInfo.setOriginPriceFen(1500);
            skuSaleInfo.setSalePriceFen(900);

            skuInfoDO.setSaleInfo(skuSaleInfo);

            SkuRestrictInfo skuRestrictInfo = new SkuRestrictInfo();
            skuRestrictInfo.setEnable(true);
            List<SkuRestrictItem> skuRestrictItems = Lists.newArrayList();
            skuRestrictInfo.setRestrictItems(skuRestrictItems);

            SkuRestrictItem item = new SkuRestrictItem();
            item.setTotal(4L);
            item.setPeriodType(RestrictPeriodType.TOTAL);
            item.setPeriodCount(14);
            item.setItemType(RestrictItemType.TOTAL);
            item.setUserTypes(Lists.newArrayList(UserTypeEnum.USERID));
            skuRestrictItems.add(item);

            skuInfoDO.setRestrictInfo(skuRestrictInfo);

            SkuFinanceInfo settleInfo = new SkuFinanceInfo();
            settleInfo.setContractorId("438098434");
            settleInfo.setSettlePriceFen(900);
            settleInfo.setFinanceProductType(1);
            settleInfo.setPeriodCycle(1);

            skuInfoDO.setFinanceInfo(settleInfo);

            SkuViewInfo viewInfo = new SkuViewInfo();
            viewInfo.setDisplayDesc("无门槛组合券15元;有效期14天;过期退;有效期内限购4次");
            viewInfo.setDisplayName("15元混合券包");
            viewInfo.setInternalDesc("无门槛组合券15元");
            viewInfo.setInternalName("15元混合券包");
            viewInfo.setDisplayImage("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.alicdn.com%2Fbao%2Fuploaded%2Fi3%2F374544688%2FO1CN016Zx2lK1kV9QkrD6gW_%21%210-item_pic.jpg&refer=http%3A%2F%2Fimg.alicdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1742951021&t=9c87d26097559e952d220dff49a9d060");
            skuInfoDO.setViewInfo(viewInfo);

            SkuPerformConfigDO skuPerformConfigDO = new SkuPerformConfigDO();
            skuInfoDO.setPerformConfig(skuPerformConfigDO);

            SkuPerformItemConfigDO skuPerformItemConfigDO = new SkuPerformItemConfigDO();
            skuPerformItemConfigDO.setTotalCount(1);
            skuPerformItemConfigDO.setBizType(BizTypeEnum.DOUYIN_COUPON_PACKAGE.getCode());
            skuPerformItemConfigDO.setCycle(1);
            skuPerformItemConfigDO.setPeriodType(PeriodTypeEnum.FIX_DAY.getCode());
            skuPerformItemConfigDO.setRightId(32424);
            skuPerformItemConfigDO.setPeriodCount(14);
            skuPerformItemConfigDO.setRightType(RightTypeEnum.COUPON.getCode());
            skuPerformItemConfigDO.setProviderId("1");
            RightViewInfo rightViewInfo = new RightViewInfo();
            rightViewInfo.setDisplayName("5元立减券");

            skuPerformItemConfigDO.setViewInfo(rightViewInfo);

            RightFinanceInfo rightFinanceInfo = new RightFinanceInfo();
            rightFinanceInfo.setContractorId("438098434");
            rightFinanceInfo.setSettlePriceFen(500);
            rightFinanceInfo.setFinanceable(true);
            rightFinanceInfo.setFinanceAssetType(RightTypeEnum.COUPON.getCode());//应该配置为实际的结算类型
            skuPerformItemConfigDO.setSettleInfo(rightFinanceInfo);

            SkuPerformItemConfigDO skuPerformItemConfigDO2 = new SkuPerformItemConfigDO();
            skuPerformItemConfigDO2.setTotalCount(1);
            skuPerformItemConfigDO2.setBizType(BizTypeEnum.DOUYIN_COUPON_PACKAGE.getCode());
            skuPerformItemConfigDO2.setCycle(1);
            skuPerformItemConfigDO2.setPeriodType(PeriodTypeEnum.FIX_DAY.getCode());
            skuPerformItemConfigDO2.setRightId(32423);
            skuPerformItemConfigDO2.setPeriodCount(14);
            skuPerformItemConfigDO2.setRightType(RightTypeEnum.COUPON.getCode());
            skuPerformItemConfigDO2.setProviderId("1");//会员价资格类
            rightViewInfo = new RightViewInfo();
            rightViewInfo.setDisplayName("10元立减券");
            skuPerformItemConfigDO2.setViewInfo(rightViewInfo);


            RightFinanceInfo rightFinanceInfo2 = new RightFinanceInfo();
            rightFinanceInfo2.setContractorId("438098434");
            rightFinanceInfo2.setSettlePriceFen(1000);
            rightFinanceInfo2.setFinanceable(true);
            rightFinanceInfo2.setFinanceAssetType(RightTypeEnum.COUPON.getCode());
            skuPerformItemConfigDO2.setSettleInfo(rightFinanceInfo2);

            skuPerformConfigDO.setConfigs(Lists.newArrayList(skuPerformItemConfigDO, skuPerformItemConfigDO2));
            skuInfoDO.setPerformConfig(skuPerformConfigDO);

            skuInfoDO.setExtra(new SkuExtra());
            return skuInfoDO;
        }

        public static SkuInfoDO buildSingleRightSku() {
            SkuInfoDO skuInfoDO = new SkuInfoDO();

            skuInfoDO.setSkuId(skuIdGenerator.incrementAndGet());
            skuInfoDO.setBizType(BizTypeEnum.DOUYIN_COUPON_PACKAGE.getCode());
            skuInfoDO.setCtime(TimeUtil.now());
            skuInfoDO.setUtime(TimeUtil.now());

            SkuSaleInfo skuSaleInfo = new SkuSaleInfo();
            skuSaleInfo.setOriginPriceFen(1000);
            skuSaleInfo.setSalePriceFen(600);

            skuInfoDO.setSaleInfo(skuSaleInfo);

            SkuRestrictInfo skuRestrictInfo = new SkuRestrictInfo();
            skuRestrictInfo.setEnable(true);
            List<SkuRestrictItem> skuRestrictItems = Lists.newArrayList();
            skuRestrictInfo.setRestrictItems(skuRestrictItems);

            SkuRestrictItem item = new SkuRestrictItem();
            item.setTotal(4L);
            item.setPeriodType(RestrictPeriodType.TOTAL);
            item.setPeriodCount(14);
            item.setItemType(RestrictItemType.TOTAL);
            item.setUserTypes(Lists.newArrayList(UserTypeEnum.USERID));
            skuRestrictItems.add(item);

            skuInfoDO.setRestrictInfo(skuRestrictInfo);

            SkuFinanceInfo settleInfo = new SkuFinanceInfo();
            settleInfo.setContractorId("438098434");
            settleInfo.setSettlePriceFen(600);
            settleInfo.setFinanceProductType(1);
            settleInfo.setPeriodCycle(1);

            skuInfoDO.setFinanceInfo(settleInfo);

            SkuViewInfo viewInfo = new SkuViewInfo();
            viewInfo.setDisplayDesc("无门槛立减券10元;有效期14天;过期退;有效期内限购4次");
            viewInfo.setDisplayName("10元立减券");
            viewInfo.setInternalDesc("无门槛立减券10元");
            viewInfo.setInternalName("10元立减券");
            viewInfo.setDisplayImage("https://img2.baidu.com/it/u=1205011088,2487597334&fm=253&fmt=auto&app=138&f=JPEG?w=530&h=500");
            skuInfoDO.setViewInfo(viewInfo);

            SkuPerformConfigDO skuPerformConfigDO = new SkuPerformConfigDO();
            skuInfoDO.setPerformConfig(skuPerformConfigDO);

            RightViewInfo rightViewInfo;

            SkuPerformItemConfigDO skuPerformItemConfigDO2 = new SkuPerformItemConfigDO();
            skuPerformItemConfigDO2.setTotalCount(1);
            skuPerformItemConfigDO2.setBizType(BizTypeEnum.DOUYIN_COUPON_PACKAGE.getCode());
            skuPerformItemConfigDO2.setCycle(1);
            skuPerformItemConfigDO2.setPeriodType(PeriodTypeEnum.FIX_DAY.getCode());
            skuPerformItemConfigDO2.setRightId(32423);
            skuPerformItemConfigDO2.setPeriodCount(14);
            skuPerformItemConfigDO2.setRightType(RightTypeEnum.COUPON.getCode());
            skuPerformItemConfigDO2.setProviderId("1");//会员价资格类
            rightViewInfo = new RightViewInfo();
            rightViewInfo.setDisplayName("10元立减券");
            skuPerformItemConfigDO2.setViewInfo(rightViewInfo);


            RightFinanceInfo rightFinanceInfo2 = new RightFinanceInfo();
            rightFinanceInfo2.setContractorId("438098434");
            rightFinanceInfo2.setSettlePriceFen(1000);
            rightFinanceInfo2.setFinanceable(true);
            rightFinanceInfo2.setFinanceAssetType(RightTypeEnum.COUPON.getCode());
            skuPerformItemConfigDO2.setSettleInfo(rightFinanceInfo2);

            skuPerformConfigDO.setConfigs(Lists.newArrayList(skuPerformItemConfigDO2));
            skuInfoDO.setPerformConfig(skuPerformConfigDO);

            skuInfoDO.setExtra(new SkuExtra());
            return skuInfoDO;
        }
    }


    public static class InitMallMemberSKU {


        public static SkuInfoDO buildMallMemberSKu() {
            SkuInfoDO skuInfoDO = new SkuInfoDO();

            skuInfoDO.setSkuId(skuIdGenerator.incrementAndGet());
            skuInfoDO.setBizType(BizTypeEnum.DEMO_MEMBER.getCode());
            skuInfoDO.setCtime(TimeUtil.now());
            skuInfoDO.setUtime(TimeUtil.now());

            SkuSaleInfo skuSaleInfo = new SkuSaleInfo();
            skuSaleInfo.setOriginPriceFen(4500);
            skuSaleInfo.setSalePriceFen(4000);

            skuInfoDO.setSaleInfo(skuSaleInfo);

            SkuFinanceInfo settleInfo = new SkuFinanceInfo();
            settleInfo.setContractorId("438098434");
            settleInfo.setSettlePriceFen(4000);
            settleInfo.setFinanceProductType(1);
            settleInfo.setPeriodCycle(3);

            skuInfoDO.setFinanceInfo(settleInfo);

            SkuViewInfo viewInfo = new SkuViewInfo();
            viewInfo.setDisplayDesc("有效期3个月;每月6张免运费券;Plus会员专属价");
            viewInfo.setDisplayName("京西Plus会员季卡");
            viewInfo.setInternalDesc("电商会员季卡");
            viewInfo.setInternalName("电商会员季卡");
            viewInfo.setDisplayImage("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.alicdn.com%2Fbao%2Fuploaded%2Fi3%2F519685624%2FO1CN01Bb37dO1rPq9taBeml_%21%21519685624.jpg&refer=http%3A%2F%2Fimg.alicdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1742992970&t=a2e8db0911c3f759c109d95b5dc0082a");
            skuInfoDO.setViewInfo(viewInfo);

            SkuPerformConfigDO skuPerformConfigDO = new SkuPerformConfigDO();
            skuInfoDO.setPerformConfig(skuPerformConfigDO);


            SkuPerformItemConfigDO skuPerformItemConfigDO = new SkuPerformItemConfigDO();
            skuPerformItemConfigDO.setTotalCount(6);
            skuPerformItemConfigDO.setBizType(1);
            skuPerformItemConfigDO.setCycle(3);
            skuPerformItemConfigDO.setPeriodType(PeriodTypeEnum.FIX_DAY.getCode());
            skuPerformItemConfigDO.setRightId(32424);
            skuPerformItemConfigDO.setPeriodCount(31);
            skuPerformItemConfigDO.setRightType(RightTypeEnum.FREE_FREIGHT_COUPON.getCode());
            skuPerformItemConfigDO.setProviderId("1");
            RightViewInfo rightViewInfo = new RightViewInfo();
            rightViewInfo.setDisplayName("免运费券");

            skuPerformItemConfigDO.setViewInfo(rightViewInfo);

            RightFinanceInfo rightFinanceInfo = new RightFinanceInfo();
            rightFinanceInfo.setContractorId("438098434");
            rightFinanceInfo.setSettlePriceFen(30);
            rightFinanceInfo.setFinanceable(true);
            rightFinanceInfo.setFinanceAssetType(RightTypeEnum.FREE_FREIGHT_COUPON.getCode());//应该配置为实际的结算类型
            skuPerformItemConfigDO.setSettleInfo(rightFinanceInfo);

            SkuPerformItemConfigDO skuPerformItemConfigDO2 = new SkuPerformItemConfigDO();
            skuPerformItemConfigDO2.setTotalCount(Integer.MAX_VALUE);
            skuPerformItemConfigDO2.setBizType(1);
            skuPerformItemConfigDO2.setCycle(3);
            skuPerformItemConfigDO2.setPeriodType(PeriodTypeEnum.FIX_DAY.getCode());
            skuPerformItemConfigDO2.setRightId(32423);
            skuPerformItemConfigDO2.setPeriodCount(31);
            skuPerformItemConfigDO2.setRightType(RightTypeEnum.MEMBER_DISCOUNT_PRICE.getCode());
            skuPerformItemConfigDO2.setProviderId("2");//会员价资格类
            rightViewInfo = new RightViewInfo();
            rightViewInfo.setDisplayName("会员价权益");
            skuPerformItemConfigDO2.setViewInfo(rightViewInfo);


            RightFinanceInfo rightFinanceInfo2 = new RightFinanceInfo();
            rightFinanceInfo2.setContractorId("438098434");
            rightFinanceInfo2.setSettlePriceFen(0);
            rightFinanceInfo2.setFinanceable(false);
            rightFinanceInfo2.setFinanceAssetType(RightTypeEnum.MEMBER_DISCOUNT_PRICE.getCode());
            skuPerformItemConfigDO2.setSettleInfo(rightFinanceInfo2);


            SkuPerformItemConfigDO skuPerformItemConfigDO3 = new SkuPerformItemConfigDO();
            skuPerformItemConfigDO3.setTotalCount(Integer.MAX_VALUE);
            skuPerformItemConfigDO3.setBizType(1);
            skuPerformItemConfigDO3.setCycle(3);
            skuPerformItemConfigDO3.setPeriodType(PeriodTypeEnum.FIX_DAY.getCode());
            skuPerformItemConfigDO3.setRightId(32425);
            skuPerformItemConfigDO3.setPeriodCount(31);
            skuPerformItemConfigDO3.setRightType(RightTypeEnum.MEMBERSHIP.getCode());
            skuPerformItemConfigDO3.setProviderId("3");//会员价资格类
            rightViewInfo = new RightViewInfo();
            rightViewInfo.setDisplayName("会员身份");
            skuPerformItemConfigDO3.setViewInfo(rightViewInfo);


            RightFinanceInfo rightFinanceInfo3 = new RightFinanceInfo();
            rightFinanceInfo3.setContractorId("438098434");
            rightFinanceInfo3.setSettlePriceFen(0);
            rightFinanceInfo3.setFinanceable(false);
            rightFinanceInfo3.setFinanceAssetType(RightTypeEnum.MEMBERSHIP.getCode());
            skuPerformItemConfigDO3.setSettleInfo(rightFinanceInfo3);

            skuPerformConfigDO.setConfigs(Lists.newArrayList(skuPerformItemConfigDO, skuPerformItemConfigDO2, skuPerformItemConfigDO3));
            skuInfoDO.setPerformConfig(skuPerformConfigDO);

            skuInfoDO.setExtra(new SkuExtra());
            return skuInfoDO;
        }


        public static SkuInfoDO buildMallMemberSKuMonth() {
            SkuInfoDO skuInfoDO = new SkuInfoDO();

            skuInfoDO.setSkuId(skuIdGenerator.incrementAndGet());
            skuInfoDO.setBizType(BizTypeEnum.DEMO_MEMBER.getCode());
            skuInfoDO.setCtime(TimeUtil.now());
            skuInfoDO.setUtime(TimeUtil.now());

            SkuSaleInfo skuSaleInfo = new SkuSaleInfo();
            skuSaleInfo.setOriginPriceFen(1800);
            skuSaleInfo.setSalePriceFen(1500);

            skuInfoDO.setSaleInfo(skuSaleInfo);

            SkuFinanceInfo settleInfo = new SkuFinanceInfo();
            settleInfo.setContractorId("438098434");
            settleInfo.setSettlePriceFen(1800);
            settleInfo.setFinanceProductType(1);
            settleInfo.setPeriodCycle(1);

            skuInfoDO.setFinanceInfo(settleInfo);

            SkuViewInfo viewInfo = new SkuViewInfo();
            viewInfo.setDisplayDesc("有效期31天;6张免运费券;Plus会员专属价");
            viewInfo.setDisplayName("京西Plus会员月卡");
            viewInfo.setInternalDesc("电商会员月卡");
            viewInfo.setInternalName("电商会员月卡");
            viewInfo.setDisplayImage("https://img0.baidu.com/it/u=973101599,73999428&fm=253&fmt=auto&app=138&f=JPEG?w=400&h=400");
            skuInfoDO.setViewInfo(viewInfo);

            SkuPerformConfigDO skuPerformConfigDO = new SkuPerformConfigDO();
            skuInfoDO.setPerformConfig(skuPerformConfigDO);

            SkuPerformItemConfigDO skuPerformItemConfigDO = new SkuPerformItemConfigDO();
            skuPerformItemConfigDO.setTotalCount(6);
            skuPerformItemConfigDO.setBizType(1);
            skuPerformItemConfigDO.setCycle(1);
            skuPerformItemConfigDO.setPeriodType(PeriodTypeEnum.FIX_DAY.getCode());
            skuPerformItemConfigDO.setRightId(32424);
            skuPerformItemConfigDO.setPeriodCount(31);
            skuPerformItemConfigDO.setRightType(RightTypeEnum.FREE_FREIGHT_COUPON.getCode());
            skuPerformItemConfigDO.setProviderId("1");
            RightViewInfo rightViewInfo = new RightViewInfo();
            rightViewInfo.setDisplayName("免运费券");

            skuPerformItemConfigDO.setViewInfo(rightViewInfo);

            RightFinanceInfo rightFinanceInfo = new RightFinanceInfo();
            rightFinanceInfo.setContractorId("438098434");
            rightFinanceInfo.setSettlePriceFen(30);
            rightFinanceInfo.setFinanceable(true);
            rightFinanceInfo.setFinanceAssetType(RightTypeEnum.FREE_FREIGHT_COUPON.getCode());//应该配置为实际的结算类型
            skuPerformItemConfigDO.setSettleInfo(rightFinanceInfo);

            SkuPerformItemConfigDO skuPerformItemConfigDO2 = new SkuPerformItemConfigDO();
            skuPerformItemConfigDO2.setTotalCount(Integer.MAX_VALUE);
            skuPerformItemConfigDO2.setBizType(1);
            skuPerformItemConfigDO2.setCycle(1);
            skuPerformItemConfigDO2.setPeriodType(PeriodTypeEnum.FIX_DAY.getCode());
            skuPerformItemConfigDO2.setRightId(32423);
            skuPerformItemConfigDO2.setPeriodCount(31);
            skuPerformItemConfigDO2.setRightType(RightTypeEnum.MEMBER_DISCOUNT_PRICE.getCode());
            skuPerformItemConfigDO2.setProviderId("2");//会员价资格类
            rightViewInfo = new RightViewInfo();
            rightViewInfo.setDisplayName("会员价权益");
            skuPerformItemConfigDO2.setViewInfo(rightViewInfo);


            RightFinanceInfo rightFinanceInfo2 = new RightFinanceInfo();
            rightFinanceInfo2.setContractorId("438098434");
            rightFinanceInfo2.setSettlePriceFen(0);
            rightFinanceInfo2.setFinanceable(false);
            rightFinanceInfo2.setFinanceAssetType(RightTypeEnum.MEMBER_DISCOUNT_PRICE.getCode());
            skuPerformItemConfigDO2.setSettleInfo(rightFinanceInfo2);


            SkuPerformItemConfigDO skuPerformItemConfigDO3 = new SkuPerformItemConfigDO();
            skuPerformItemConfigDO3.setTotalCount(Integer.MAX_VALUE);
            skuPerformItemConfigDO3.setBizType(1);
            skuPerformItemConfigDO3.setCycle(1);
            skuPerformItemConfigDO3.setPeriodType(PeriodTypeEnum.FIX_DAY.getCode());
            skuPerformItemConfigDO3.setRightId(32425);
            skuPerformItemConfigDO3.setPeriodCount(31);
            skuPerformItemConfigDO3.setRightType(RightTypeEnum.MEMBERSHIP.getCode());
            skuPerformItemConfigDO3.setProviderId("3");//会员价资格类
            rightViewInfo = new RightViewInfo();
            rightViewInfo.setDisplayName("会员身份");
            skuPerformItemConfigDO3.setViewInfo(rightViewInfo);


            RightFinanceInfo rightFinanceInfo3 = new RightFinanceInfo();
            rightFinanceInfo3.setContractorId("438098434");
            rightFinanceInfo3.setSettlePriceFen(0);
            rightFinanceInfo3.setFinanceable(false);
            rightFinanceInfo3.setFinanceAssetType(RightTypeEnum.MEMBERSHIP.getCode());
            skuPerformItemConfigDO3.setSettleInfo(rightFinanceInfo3);

            skuPerformConfigDO.setConfigs(Lists.newArrayList(skuPerformItemConfigDO, skuPerformItemConfigDO2, skuPerformItemConfigDO3));
            skuInfoDO.setPerformConfig(skuPerformConfigDO);

            skuInfoDO.setExtra(new SkuExtra());
            return skuInfoDO;
        }
    }
}
