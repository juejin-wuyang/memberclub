/**
 * @(#)PurchaseBizService.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.purchase.service.biz;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.log.LogDomainEnum;
import com.memberclub.common.log.UserLog;
import com.memberclub.common.retry.Retryable;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.purchase.PurchaseSubmitCmd;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.context.purchase.PurchaseSubmitResponse;
import com.memberclub.domain.context.purchase.cancel.PurchaseCancelCmd;
import com.memberclub.domain.context.purchase.cancel.PurchaseCancelContext;
import com.memberclub.domain.context.purchase.common.MemberOrderStatusEnum;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.exception.MemberException;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.payment.context.PaymentTimeoutMessage;
import com.memberclub.sdk.memberorder.domain.MemberOrderDomainService;
import com.memberclub.sdk.memberorder.domain.OrderRemarkBuilder;
import com.memberclub.sdk.purchase.extension.PurchaseExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class PurchaseBizService {

    @Autowired
    private ExtensionManager em;
    @Autowired
    private MemberOrderDomainService memberOrderDomainService;

    public void cancel(PurchaseCancelCmd cmd) {
        PurchaseCancelContext context = new PurchaseCancelContext();
        context.setCmd(cmd);
        try {
            em.getExtension(BizScene.of(context.getCmd().getBizType()),
                    PurchaseExtension.class).cancel(context);
        } catch (MemberException e) {
            throw e;
        } catch (Exception e) {
            MemberException me = ResultCode.PURCHASE_CANCEL_ERROR.newException("开通取消流程异常", e);
            throw me;
        }
    }

    public void reverse(AfterSaleApplyContext context) {
        try {
            em.getExtension(context.getApplyCmd().getBizType().toBizScene(), PurchaseExtension.class).reverse(context);
        } catch (MemberException e) {
            throw e;
        } catch (Exception e) {
            MemberException me = ResultCode.PURCHASE_REVERSE_ERROR.newException("开通逆向流程异常", e);
            throw me;
        }
    }


    @UserLog(domain = LogDomainEnum.PURCHASE)
    public PurchaseSubmitResponse submit(PurchaseSubmitCmd cmd) {
        cmd.isValid();

        PurchaseSubmitContext context = new PurchaseSubmitContext(cmd);
        PurchaseSubmitResponse response = new PurchaseSubmitResponse();
        try {
            em.getExtension(
                    context.toDefaultBizScene(), PurchaseExtension.class).submit(context);
            context.monitor();

            if (context.getMemberOrder().getStatus() == MemberOrderStatusEnum.SUBMITED) {
                response.setSuccess(true);
                response.setMemberOrderDO(context.getMemberOrder());
                response.setLockValue(context.getLockValue());
                return response;
            }
            // TODO: 2025/1/4 补充错误信息
            response.setSuccess(false);
            return response;
        } catch (MemberException e) {
            context.monitorException(e);
            throw e;
        } catch (Exception e) {
            MemberException me = ResultCode.COMMON_ORDER_SUBMIT_ERROR.newException("提单流程异常", e);
            context.monitorException(me);
            throw me;
        }
        // TODO: 2025/1/4 补充返回值
    }

    @Retryable(throwException = false)
    @UserLog(domain = LogDomainEnum.PURCHASE)
    public void paymentTimeoutValidate(PaymentTimeoutMessage message) {
        MemberOrderDO memberOrderDO = memberOrderDomainService.getMemberOrderDO(message.getUserId(), message.getTradeId());
        if (memberOrderDO == null) {
            CommonLog.error("收到支付过期事件，未查询到MemberOrder message:{}", message);
            return;
        }
        if (memberOrderDO.getPaymentInfo() == null) {
            CommonLog.error("收到支付过期事件，未查询到MemberOrder.payment message:{}", message);
            return;
        }

        if (memberOrderDO.getPaymentInfo().getPayStatus().isPaid()) {
            CommonLog.warn("收到支付过期事件，MemberOrder 已支付无需处理， payStatus:{}, message:{} ",
                    memberOrderDO.getPaymentInfo().getPayStatus(), message);
            return;
        }

        if (memberOrderDO.getStatus() != MemberOrderStatusEnum.SUBMITED) {
            CommonLog.error("收到支付过期事件，MemberOrder 状态不合法，跳过不处理 status:{}, message:{} ",
                    memberOrderDO.getStatus(), message);
            return;
        }

        if (TimeUtil.now() > message.getPayExpireTime()) {
            CommonLog.error("未到支付超时时间，收到支付超时事件:{}", message);
            //继续处理
        }

        CommonLog.warn("收到支付超时检查，开始取消订单 msg:{}", message);

        OrderRemarkBuilder.builder(message.getBizType(), message.getUserId(), message.getTradeId())
                .remark("订单支付超时，需要取消订单").save();
        cancelOrder(message);
    }

    private void cancelOrder(PaymentTimeoutMessage message) {
        PurchaseCancelCmd cancelCmd = new PurchaseCancelCmd();
        cancelCmd.setBizType(BizTypeEnum.findByCode(message.getBizType()));
        cancelCmd.setTradeId(message.getTradeId());
        cancelCmd.setUserId(message.getUserId());
        cancel(cancelCmd);
    }

}