/**
 * @(#)RetryableAspect.java, 十二月 29, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.common.retry;

import com.google.common.base.Defaults;
import com.google.common.collect.Lists;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.RetryableContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 */
@Aspect
@Component
public class RetryableAspect {

    @Autowired
    private RetryService retryService;

    public static String toLowerCaseFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        char firstChar = str.charAt(0);
        char updatedFirstChar = Character.toLowerCase(firstChar);
        String remainder = str.substring(1);
        return updatedFirstChar + remainder;
    }

    @Pointcut("@annotation(Retryable) && execution(public * *(..))")
    public void pointcut() {
    }

    @Around(value = "pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //无参方法不处理
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object[] args = joinPoint.getArgs();

        //获取注解
        Retryable annotation = method.getAnnotation(Retryable.class);
        if (annotation != null && args != null && args.length > 0) {
            try {
                Object response = joinPoint.proceed();
                return response;
            } catch (Exception e) {
                Object param = joinPoint.getArgs()[0];

                String beanName = toLowerCaseFirst(joinPoint.getSignature().getDeclaringType().getSimpleName());
                String methoName = ((MethodSignature) joinPoint.getSignature()).getMethod().getName();

                int retryTimes = RetryLocalContext.getRetryTimes(beanName, methoName);
                retryTimes++;

                if (retryTimes > annotation.maxTimes()) {
                    // TODO: 2024/12/31 fallback逻辑
                    CommonLog.error("超过最大重试次数 maxTimes:{}, methodName:{} ,params:{}",
                            annotation.maxTimes(), method.getName(), args);
                    throw e;
                }

                if (param instanceof RetryableContext) {
                    RetryableContext retryableContext = ((RetryableContext) param);
                    retryableContext.setRetryTimes(retryTimes);
                }

                int initialDelayTime = annotation.initialDelaySeconds();
                double multiplier = annotation.multiplier();

                long delayTime = initialDelayTime * (int) Math.pow(multiplier, retryTimes - 1);
                delayTime = delayTime > annotation.maxDelaySeconds() ? annotation.maxDelaySeconds() : delayTime;

                RetryMessage message = new RetryMessage();
                message.setBeanName(beanName);
                message.setBeanClassName(joinPoint.getSignature().getDeclaringType().getName());
                message.setMethodName(methoName);
                message.setThrowException(annotation.throwException());

                List<String> argsList = Lists.newArrayList(args).stream().map(RetryUtil::toJson).collect(Collectors.toList());

                List<String> argsClassList = Arrays.stream(((MethodSignature) joinPoint.getSignature()).getParameterTypes())
                        .map(Class::getName).collect(Collectors.toList());

                message.setArgsList(argsList);
                message.setArgsClassList(argsClassList);
                message.setRetryTimes(retryTimes);
                message.setExpectedTime(TimeUtil.now() + TimeUnit.SECONDS.toMillis(delayTime));

                retryService.addRetryMessage(message);
                if (annotation.throwException()) {
                    throw e;
                } else {
                    CommonLog.error("执行重试方式异常 message:{}", message, e);
                }
                return Defaults.defaultValue(method.getReturnType());
            }
        } else {
            return joinPoint.proceed();
        }
    }
}