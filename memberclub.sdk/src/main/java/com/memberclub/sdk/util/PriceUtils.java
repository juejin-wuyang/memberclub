package com.memberclub.sdk.util;

import java.math.BigDecimal;

public class PriceUtils {
    public static String change2Yuan(int money) {
        BigDecimal base = BigDecimal.valueOf(money);
        BigDecimal yuanBase = base.divide(new BigDecimal(100));
        return yuanBase.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    public static int change2Fen(String money) {
        BigDecimal base = new BigDecimal(money);

        BigDecimal fenBase = base.multiply(new BigDecimal(100));
        return fenBase.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
    }
}
