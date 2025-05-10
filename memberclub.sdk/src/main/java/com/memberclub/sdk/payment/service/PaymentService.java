package com.memberclub.sdk.payment.service;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.log.LogDomainEnum;
import com.memberclub.common.log.UserLog;
import com.memberclub.common.retry.Retryable;
import com.memberclub.common.util.JsonUtils;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.aftersale.apply.AftersaleApplyCmd;
import com.memberclub.domain.context.aftersale.apply.AftersaleApplyResponse;
import com.memberclub.domain.context.aftersale.contant.AftersaleSourceEnum;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.context.purchase.common.MemberOrderStatusEnum;
import com.memberclub.domain.dataobject.payment.PrePayResult;
import com.memberclub.domain.dataobject.payment.context.PaymentNotifyCmd;
import com.memberclub.domain.dataobject.payment.context.PaymentNotifyContext;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.exception.MemberException;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.mq.MQTopicEnum;
import com.memberclub.infrastructure.mq.MessageQuenePublishFacade;
import com.memberclub.infrastructure.payment.PaymentFacadeSPI;
import com.memberclub.infrastructure.payment.context.*;
import com.memberclub.sdk.aftersale.service.AfterSaleBizService;
import com.memberclub.sdk.memberorder.domain.MemberOrderDomainService;
import com.memberclub.sdk.payment.PaymentDataObjectFactory;
import com.memberclub.sdk.payment.extension.PaymentExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private MemberOrderDomainService memberOrderDomainService;

    @Autowired
    private ExtensionManager extensionManager;

    @Autowired
    private PaymentFacadeSPI paymentFacadeSPI;

    @Autowired
    private PaymentDataObjectFactory paymentDataObjectFactory;

    @Autowired
    private MessageQuenePublishFacade messageQuenePublishFacade;

    @Autowired
    private AfterSaleBizService aftersaleBizService;

    private static PaymentNotifyContext buildNotifyContext(PaymentNotifyCmd cmd, MemberOrderDO memberOrderDO) {
        PaymentNotifyContext context = new PaymentNotifyContext();
        context.setMemberOrderDO(memberOrderDO);
        context.setTradeId(cmd.getTradeId());
        context.setCmd(cmd);
        context.setUserId(cmd.getUserId());
        context.setBizType(memberOrderDO.getBizType());
        return context;
    }

    public void paymentRefund(AfterSaleApplyContext context) {
        // 调用支付原路退款
        PaymentExtension paymentExtension = extensionManager.getExtension(context.toBizScene(), PaymentExtension.class);
        paymentExtension.initializePaymentRefundOrder(context);

        PaymentRefundRequestDTO request = paymentDataObjectFactory.createPaymentRefundRequestDTO(context);

        PaymentRefundResponseDTO response = null;
        try {
            response = paymentFacadeSPI.refundPayOrder(request);
        } catch (Exception e) {
            CommonLog.warn("支付单退款异常 request:{}", request, e);
            throw ResultCode.PAY_REFUND_EXCEPTION.newException("支付单退款异常", e);
        }

        if (!response.isSuccess()) {
            CommonLog.error("支付单退款失败 request:{}, response:{}", request, response);
            throw ResultCode.PAY_REFUND_EXCEPTION.newException("支付单退款失败:" + response.getMsg());
        }
        if (response.getRefundAmountFen() != request.getRefundAmountFen()) {
            CommonLog.error("支付单退款失败 退款金额不一致 request:{}, response:{}", request, response);
            throw ResultCode.PAY_REFUND_EXCEPTION.newException("支付单退款失败 退款金不一致");
        }
        CommonLog.warn("调用支付退款成功 result:{}", response);
    }

    /**
     * 支付成功事件消费处理
     *
     * @param cmd
     */
    @UserLog(domain = LogDomainEnum.PURCHASE)
    @Retryable(throwException = false)
    public void paymentNotify(PaymentNotifyCmd cmd) {
        MemberOrderDO memberOrderDO = memberOrderDomainService.getMemberOrderDO(cmd.getUserId(), cmd.getTradeId());
        if (memberOrderDO == null) {
            CommonLog.error("未查询到订单信息:{}", cmd);
            return;
        }
        PaymentNotifyContext context = buildNotifyContext(cmd, memberOrderDO);
        if (context.isOrderCancel()) {
            memberOrderDomainService.onPaySuccess4OrderTimeout(context, memberOrderDO);
            CommonLog.warn("收到支付成功事件，订单状态已取消，需要原路退款");
            //调用售后仅退款
            refund(context, memberOrderDO);
            return;
        }
        if (context.isOrderRefund()) {
            CommonLog.error("订单已经退款完成 order:{}", memberOrderDO);
            return;
        }
        if (context.isPaid()) {
            CommonLog.warn("订单已经支付完成 status:{}", memberOrderDO.getStatus());
            return;
        }
        //todo 如果可以调用一下支付风控
        PaymentExtension paymentExtension = extensionManager.getExtension(BizScene.of(context.getBizType()), PaymentExtension.class);
        try {
            paymentExtension.validateAmount4Notify(context);
            paymentExtension.validate4BizException(context);
        } catch (MemberException e) {
            if (e.getCode() == ResultCode.PAY_EXCEPTION) {
                CommonLog.error("支付异常", e);
                return;
            }
        }


        //修改数据库,内部有重试
        memberOrderDomainService.onPaySuccess(context, memberOrderDO);
    }

    private void refund(PaymentNotifyContext context, MemberOrderDO memberOrderDO) {
        PaymentNotifyCmd cmd = context.getCmd();
        AftersaleApplyCmd applyCmd = new AftersaleApplyCmd();
        applyCmd.setReason("支付成功时，订单已经超时退款，因此原路退款");
        applyCmd.setOperator("system");
        applyCmd.setUserId(cmd.getUserId());
        applyCmd.setSource(AftersaleSourceEnum.SYSTEM_REFUND_4_ORDER_PAY_TIMEOUT);
        applyCmd.setBizType(memberOrderDO.getBizType());
        applyCmd.setTradeId(cmd.getTradeId());
        AftersaleApplyResponse response = aftersaleBizService.apply4RefundOnly(applyCmd);
        if (!response.isSuccess()) {
            ResultCode resultCode = ResultCode.findByCode(response.getUnableCode());
            if (resultCode != null && resultCode.isNeedRetry()) {
                CommonLog.error("调用售后退款失败，需要重试 response:{}", response);
                throw resultCode.newException();
            } else {
                CommonLog.warn("调用售后退款失败，不需要重试 response:{}", response);
            }
            return;
        }
        memberOrderDomainService.onRefund4OrderTimeout(context, memberOrderDO);
        CommonLog.warn("调用售后退款成功response:{}", response);
    }

    public void prePay(PurchaseSubmitContext context) {
        if (context.getMemberOrder().getStatus() != MemberOrderStatusEnum.INIT) {
            throw ResultCode.PRE_PAY_EXCEPTION.newException("创建预支付单异常订单状态非提交状态:" + context.getMemberOrder().getStatus().toString());
        }

        PaymentExtension paymentExtension = extensionManager.getExtension(context.toDefaultBizScene(), PaymentExtension.class);
        paymentExtension.initializePayment(context);

        //创建PrePayRequest
        PrePayRequestDTO requestDTO = paymentDataObjectFactory.createPrePayRequestDTO(context);
        PrePayResponseDTO response = null;
        try {
            response = paymentFacadeSPI.createPayOrder(requestDTO);
        } catch (Exception e) {
            CommonLog.warn("创建预支付单异常 request:{}", requestDTO, e);
            throw ResultCode.PRE_PAY_EXCEPTION.newException("创建预支付单异常", e);
        }

        if (!response.isSuccess()) {
            CommonLog.warn("创建预支付单失败 request:{}, response:{}", requestDTO, response);
            throw ResultCode.PRE_PAY_EXCEPTION.newException("创建预支付单失败:" + response.getMsg());
        }
        PrePayResult result = paymentDataObjectFactory.convertPayResponse(response);
        context.handlePrePayResult(result);

        CommonLog.warn("调用预支付成功 result:{}", result);

        //更新支付状态
        memberOrderDomainService.onPrePay(context, context.getMemberOrder());

        //todo 创建消息拉取支付结果，推拉结合

        registerPaymentTimeoutValidator(context);
        //处理返回值
    }

    public void registerPaymentTimeoutValidator(PurchaseSubmitContext context) {
        PaymentTimeoutMessage message = new PaymentTimeoutMessage();
        message.setBizType(context.getBizType().getCode());
        message.setTradeId(context.getMemberOrder().getTradeId());
        message.setUserId(context.getMemberOrder().getUserId());
        message.setPayExpireTime(context.getPayExpireTime());
        //创建延迟消息，检查支付状态
        messageQuenePublishFacade.publish(MQTopicEnum.TRADE_PAYMENT_TIMEOUT_EVENT, JsonUtils.toJson(message));
    }
}
