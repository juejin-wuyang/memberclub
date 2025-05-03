package com.memberclub.sdk.payment.service;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.log.LogDomainEnum;
import com.memberclub.common.log.UserLog;
import com.memberclub.common.util.JsonUtils;
import com.memberclub.domain.common.BizScene;
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
import com.memberclub.infrastructure.payment.context.PayExpireCheckMessage;
import com.memberclub.infrastructure.payment.context.PrePayRequestDTO;
import com.memberclub.infrastructure.payment.context.PrePayResponseDTO;
import com.memberclub.sdk.event.trade.service.domain.TradeEventDomainService;
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
    private TradeEventDomainService tradeEventDomainService;

    private static PaymentNotifyContext buildNotifyContext(PaymentNotifyCmd cmd, MemberOrderDO memberOrderDO) {
        PaymentNotifyContext context = new PaymentNotifyContext();
        context.setMemberOrderDO(memberOrderDO);
        context.setTradeId(cmd.getTradeId());
        context.setCmd(cmd);
        context.setUserId(cmd.getUserId());
        context.setBizType(memberOrderDO.getBizType());
        return context;
    }

    @UserLog(domain = LogDomainEnum.PURCHASE)
    public void paymentNotify(PaymentNotifyCmd cmd) {
        MemberOrderDO memberOrderDO = memberOrderDomainService.getMemberOrderDO(cmd.getUserId(), cmd.getTradeId());
        if (memberOrderDO == null) {
            CommonLog.error("未查询到订单信息:{}", cmd);
            return;
        }
        PaymentNotifyContext context = buildNotifyContext(cmd, memberOrderDO);
        if (context.isOrderCancel()) {
            CommonLog.warn("收到支付成功事件，订单状态已取消，需要原路退款");
            //调用售后仅退款
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
        //发布支付事件
        tradeEventDomainService.publishEventOnPaySuccess(context);

        //修改数据库,内部有重试
        memberOrderDomainService.onPaySuccess(context, memberOrderDO);
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
        PayExpireCheckMessage message = new PayExpireCheckMessage();
        message.setBizType(context.getBizType().getCode());
        message.setTradeId(context.getMemberOrder().getTradeId());
        message.setUserId(context.getMemberOrder().getUserId());
        message.setPayExpireTime(context.getPayExpireTime());
        //创建延迟消息，检查支付状态
        messageQuenePublishFacade.publish(MQTopicEnum.TRADE_PAY_EXPIRE_CHECK, JsonUtils.toJson(message));
    }
}
