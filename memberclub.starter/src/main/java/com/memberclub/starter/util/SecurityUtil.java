package com.memberclub.starter.util;

import javax.servlet.http.HttpServletRequest;

public class SecurityUtil {

    private static ThreadLocal<SecurityInfo> threadLocal = new ThreadLocal<>();

    public static long getUserId() {
        return threadLocal.get().getUserId();
    }

    public static void securitySet(HttpServletRequest request) {
        SecurityInfo securityInfo = new SecurityInfo();
        securityInfo.setUserId(Long.parseLong(request.getHeader("user_id")));
        threadLocal.set(securityInfo);
    }

    public static void clear() {
        threadLocal.remove();
    }
}
