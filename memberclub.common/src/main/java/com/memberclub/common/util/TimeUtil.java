/**
 * @(#)TimeUtil.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.common.util;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 掘金五阳
 */
public class TimeUtil {

    private static final String FORMAT = "yy-MM-dd HH:mm:ss";

    private static final String FORMAT_DAY = "yy.MM.dd";

    public static long now() {
        return System.currentTimeMillis();
    }

    public static String format(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT);
        return dateFormat.format(new Date(time));
    }


    public static String formatDay(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DAY);
        return dateFormat.format(new Date(time));
    }

    public static long plusGivenDayEtimeFromNow(int days) {
        return plusGivenDayEtime(now(), days);
    }

    public static long plusGivenDayEtime(long time, int days) {
        DateTime dateTime = getDateTime(time);

        return dateTime.plusDays(days).withHourOfDay(23)
                .withMinuteOfHour(59)
                .withSecondOfMinute(59)
                .withMillisOfSecond(999)
                .getMillis();
    }

    public static DateTime getDateTime(long time) {
        return new DateTime(time);
    }


}