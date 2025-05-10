package com.memberclub.sdk.common;

import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewContext;
import com.memberclub.infrastructure.dynamic_config.SwitchEnum;
import org.springframework.stereotype.Service;

@Service
public class DegradeSwitchService {

    public static boolean degrade4AfterSale(AfterSalePreviewContext context) {
        boolean degrade = SwitchEnum.AFTERSALE_DEGRADE.getBoolean(context.getCmd().getBizType().getCode(),
                String.valueOf(context.getCmd().getSource().getCode()));
        return degrade;
    }
}
