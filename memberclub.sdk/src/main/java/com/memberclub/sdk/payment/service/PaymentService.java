package com.memberclub.sdk.payment.service;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.util.JsonUtils;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.context.purchase.common.MemberOrderStatusEnum;
import com.memberclub.domain.dataobject.payment.PrePayResult;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.mq.MQTopicEnum;
import com.memberclub.infrastructure.mq.MessageQuenePublishFacade;
import com.memberclub.infrastructure.payment.PaymentFacadeSPI;
import com.memberclub.infrastructure.payment.context.PayExpireCheckMessage;
import com.memberclub.infrastructure.payment.context.PrePayRequestDTO;
import com.memberclub.infrastructure.payment.context.PrePayResponseDTO;
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

    private void registerPaymentTimeoutValidator(PurchaseSubmitContext context) {
        PayExpireCheckMessage message = new PayExpireCheckMessage();
        message.setBizType(context.getBizType().getCode());
        message.setTradeId(context.getMemberOrder().getTradeId());
        message.setUserId(context.getMemberOrder().getUserId());
        message.setPayExpireTime(context.getPayExpireTime());
        //创建延迟消息，检查支付状态
        messageQuenePublishFacade.publish(MQTopicEnum.TRADE_PAY_EXPIRE_CHECK, JsonUtils.toJson(message));
    }
}
