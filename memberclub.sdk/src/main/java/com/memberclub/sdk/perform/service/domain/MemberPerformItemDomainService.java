/**
 * @(#)MemberPerformItemDomainService.java, 一月 11, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.service.domain;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.collect.Lists;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.flow.SkipException;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.retry.Retryable;
import com.memberclub.common.util.CollectionUtilEx;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.aftersale.contant.RefundTypeEnum;
import com.memberclub.domain.context.perform.PerformItemContext;
import com.memberclub.domain.context.perform.common.PerformItemStatusEnum;
import com.memberclub.domain.context.perform.reverse.PerformItemReverseInfo;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.domain.context.perform.reverse.SubOrderReversePerformContext;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.domain.entity.trade.MemberPerformItem;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.mybatis.mappers.trade.MemberPerformItemDao;
import com.memberclub.sdk.common.Monitor;
import com.memberclub.sdk.perform.extension.execute.MemberPerformItemRepositoryExtension;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 */
@DS("tradeDataSource")
@Service
public class MemberPerformItemDomainService {

    @Autowired
    PerformDataObjectBuildFactory performDataObjectBuildFactory;
    @Autowired
    private ExtensionManager extensionManager;
    @Autowired
    private MemberPerformItemDao memberPerformItemDao;

    public List<MemberPerformItemDO> batchQueryItems(Long userId, List<String> itemTokens) {
        LambdaQueryWrapper<MemberPerformItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberPerformItem::getUserId, userId)
                .in(MemberPerformItem::getItemToken, itemTokens);
        List<MemberPerformItem> items = memberPerformItemDao.selectList(wrapper);
        return CollectionUtilEx.map(items, performDataObjectBuildFactory::toMemberPerformItemDO);
    }

    @Retryable(initialDelaySeconds = 1, throwException = false)
    @Transactional(rollbackFor = Exception.class)
    public void onPerformSuccess(PerformItemContext context) {
        for (MemberPerformItemDO item : context.getItems()) {
            item.onFinishPerform(context);
            LambdaUpdateWrapper<MemberPerformItem> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(MemberPerformItem::getUserId, context.getUserId())
                    .eq(MemberPerformItem::getItemToken, item.getItemToken())
                    .set(MemberPerformItem::getBatchCode, item.getBatchCode())
                    .set(MemberPerformItem::getStatus, item.getStatus().getCode())
                    .set(MemberPerformItem::getUtime, item.getUtime())
            ;

            extensionManager.getExtension(BizScene.of(context.getBizType()),
                    MemberPerformItemRepositoryExtension.class).onPerformSuccess(item, context, wrapper);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int insertMemberPerformItems(List<MemberPerformItem> items) {
        int count = memberPerformItemDao.insertIgnoreBatch(items);
        return count;
    }

    @Transactional(rollbackFor = Exception.class, noRollbackFor = SkipException.class)
    public void createPerformItems(PerformItemContext context, List<MemberPerformItem> items) {
        int count = insertMemberPerformItems(items);
        if (count >= items.size()) {
            CommonLog.warn("当前请求写入成功 member_perform_item actual:{}, expect:{}, context:{}",
                    count, items.size(), context);
            Monitor.PERFORM_EXECUTE.counter(context.getBizType(),
                    "insert_item", true,
                    "right_type", context.getRightType());
            return;
        }

        CommonLog.warn("当前请求写入 member_perform_item数量不足 actual:{}, expect:{}, context:{}",
                count, items.size(), context);
        List<MemberPerformItemDO> memberPerformItemDoList = Lists.newArrayList();

        for (MemberPerformItemDO item : context.getItems()) {
            MemberPerformItem itemInDb =
                    memberPerformItemDao.queryByItemToken(context.getUserId(), item.getItemToken());
            if (itemInDb == null) {
                Monitor.PERFORM_EXECUTE.counter(context.getBizType(),
                        "insert_item", false,
                        "right_type", context.getRightType());
                throw ResultCode.INTERNAL_ERROR.newException("member_perform_item写入失败,稍后重试");
            }
            if (!PerformItemStatusEnum.hasPerformed(itemInDb.getStatus())) {
                memberPerformItemDoList.add(item);
            }
        }
        if (CollectionUtils.isEmpty(memberPerformItemDoList)) {
            Monitor.PERFORM_EXECUTE.counter(context.getBizType(),
                    "insert_item", "duplicated",
                    "right_type", context.getRightType());
            CommonLog.warn("已完成履约:{}", context.getRightType());
            throw new SkipException();
        }
        CommonLog.warn("当前是重试请求, member_perform_item已被幂等写入 dbExistSize:{}, insert:{}",
                memberPerformItemDoList.size(), items.size());

        context.setItems(memberPerformItemDoList);
    }

    @Transactional(rollbackFor = Exception.class, noRollbackFor = SkipException.class)
    public void onStartReversePerform(ReversePerformContext context,
                                      SubOrderReversePerformContext subOrderReversePerformContext,
                                      List<PerformItemReverseInfo> infos) {
        for (PerformItemReverseInfo info : infos) {
            LambdaUpdateWrapper<MemberPerformItem> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(MemberPerformItem::getUserId, context.getUserId())
                    .eq(MemberPerformItem::getItemToken, info.getItemToken())
                    .set(MemberPerformItem::getStatus, PerformItemStatusEnum.REVEREING.getCode())
                    .set(MemberPerformItem::getUtime, TimeUtil.now())
            ;
            extensionManager.getExtension(BizScene.of(context.getBizType()), MemberPerformItemRepositoryExtension.class)
                    .onStartReversePerform(context, subOrderReversePerformContext, info, wrapper);
            CommonLog.info("更新履约项状态为逆向履约中 itemToken:{}, status:{}", info.getItemToken(), PerformItemStatusEnum.REVEREING);
        }
    }


    public boolean isFinishReversePerform(ReversePerformContext context,
                                          SubOrderReversePerformContext hisInfo,
                                          List<PerformItemReverseInfo> infos) {
        List<String> itemTokens = infos.stream().map(PerformItemReverseInfo::getItemToken).collect(Collectors.toList());
        boolean allFinish = true;
        List<MemberPerformItem> items = memberPerformItemDao.queryByItemTokens(context.getUserId(), itemTokens);
        for (PerformItemReverseInfo info : infos) {
            PerformItemStatusEnum status = generatePerformItemFinishReverseStatus(info);
            boolean finish = false;
            for (MemberPerformItem item : items) {
                if (StringUtils.equals(info.getItemToken(), item.getItemToken()) &&
                        item.getStatus() == status.getCode()) {
                    finish = true;
                }
            }
            if (!finish) {
                allFinish = false;
            }
        }
        if (allFinish) {
            return true;
        }
        return false;
    }

    private PerformItemStatusEnum generatePerformItemFinishReverseStatus(PerformItemReverseInfo info) {
        return info.getRefundType() == RefundTypeEnum.ALL_REFUND ? PerformItemStatusEnum.COMPLETED_REVERSED :
                PerformItemStatusEnum.PORTION_REVERSED;
    }

    @Transactional(rollbackFor = Exception.class, noRollbackFor = SkipException.class)
    public void onReversePerformSuccess(ReversePerformContext context,
                                        SubOrderReversePerformContext subOrderReversePerformContext,
                                        List<PerformItemReverseInfo> infos) {
        for (PerformItemReverseInfo info : infos) {
            PerformItemStatusEnum status = generatePerformItemFinishReverseStatus(info);

            LambdaUpdateWrapper<MemberPerformItem> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(MemberPerformItem::getUserId, context.getUserId())
                    .eq(MemberPerformItem::getItemToken, info.getItemToken())
                    .set(MemberPerformItem::getStatus, status.getCode())
                    .set(MemberPerformItem::getUtime, TimeUtil.now())
            ;
            extensionManager.getExtension(BizScene.of(context.getBizType()), MemberPerformItemRepositoryExtension.class)
                    .onReversePerformSuccess(context, subOrderReversePerformContext, info, wrapper);

            CommonLog.info("更新履约项状态为逆向履约完成 itemToken:{}, status:{}", info.getItemToken(), status);
        }
    }


}