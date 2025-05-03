package com.memberclub.domain.dataobject.payment.context;

import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.purchase.common.MemberOrderStatusEnum;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import lombok.Data;

@Data
public class PaymentNotifyContext {

    private BizTypeEnum bizType;

    private long userId;

    private String tradeId;

    private PaymentNotifyCmd cmd;

    private MemberOrderDO memberOrderDO;

    public boolean isOrderCancel() {
        if (memberOrderDO.getStatus() == MemberOrderStatusEnum.CANCELED) {
            return true;
        }
        return false;
    }

    public boolean isOrderRefund() {
        if (memberOrderDO.getStatus() == MemberOrderStatusEnum.COMPLETE_REFUNDED ||
                memberOrderDO.getStatus() == MemberOrderStatusEnum.PORTION_REFUNDED) {
            return true;
        }
        return false;
    }

    public boolean isPaid() {
        return memberOrderDO.getStatus().isPaid();
    }


}
