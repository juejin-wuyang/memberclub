/**
 * @(#)InfrastructureImplChecker.java, 十二月 26, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.config;

import com.memberclub.common.util.ApplicationContextUtils;
import com.memberclub.infrastructure.dynamic_config.DynamicConfig;
import com.memberclub.infrastructure.id.IdGenerator;
import com.memberclub.infrastructure.lock.DistributeLock;
import com.memberclub.infrastructure.mq.MessageQuenePublishFacade;
import lombok.Data;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;

import static org.springframework.util.Assert.notNull;

/**
 * author: 掘金五阳
 * 添加配置项,编译完成后, IDE可以在 yml 文件中推荐该配置项
 */

@Configuration
@Order(2)
@Data
@ConfigurationProperties(prefix = "memberclub.infrastructure")
public class InfrastructureImplChecker {


    /**
     * 分布式Id 类型, none 为随机生成; 默认类型为 redis
     */
    private DistributedIdEnum id;

    /**
     * 分布式锁类型,none 为不加锁,默认是 redis
     */
    private DistributedLockEnum lock;

    /**
     * 配置中心类型,none为取默认值; 默认类型为apollo(携程开源配置中心)
     */
    private DistributedConfigEnum config;


    /***
     * 自动重试注解 延迟重试能力依赖的组件
     */
    private DistributedRetryEnum retry;

    /**
     * 分布式消息队列
     */
    private DistributedMQEnum mq;

    /**
     * 订单中心
     */
    private OrderCenterEnum order;

    /**
     * 资产服务
     */
    private AssetCenterEnum asset;

    /**
     * 支付相關
     */
    private PaymentEnum payment;

    /**
     * 缓存
     */
    private DistributedCacheEnum cache;

    /**
     * userTag
     */
    private DistributedUserTagEnum usertag;
    /**
     *
     */
    private SkuAccessEnum sku;

    @NestedConfigurationProperty()
    private Feign feign;

    /**
     * 优惠金额计算
     */
    private AmountComputeEnum amountcompute;

    @PostConstruct
    public void init() {
        ApplicationContext applicationContext = ApplicationContextUtils.getContext();
        notNull(applicationContext, "未获取到 ApplicationContext");
        check(() -> applicationContext.getBean(DynamicConfig.class),
                "DynamicConfig 未获取到实现类,请关注 配置项 memberclub.infrastructure.config,默认 apollo ");

        check(() -> applicationContext.getBean(DistributeLock.class),
                "DistributeLock 未获取到实现类,请关注 配置项 memberclub.infrastructure.lock,默认 redis ");

        check(() -> applicationContext.getBean(IdGenerator.class),
                "IdGenerator 未获取到实现类,请关注 配置项 memberclub.infrastructure.id,默认 redis ");

        check(() -> applicationContext.getBean(MessageQuenePublishFacade.class),
                "MessageQuenePublishFacade 未获取到实现类,请关注 配置项 memberclub.infrastructure.mq,默认 rabbitmq ");

    }

    public void check(Runnable runnable, String msg) {
        try {
            runnable.run();
        } catch (NoSuchBeanDefinitionException e) {
            throw new RuntimeException(msg, e);
        }
    }

    @Data
    class Feign {

        /**
         * 是否开启 feign
         */

        private Boolean enabled;
    }
}