/**
 * @(#)ApolloDynamicConfig.java, 一月 18, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.dynamic_config.impl;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.memberclub.infrastructure.dynamic_config.DynamicConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@EnableApolloConfig
@ConditionalOnProperty(name = "memberclub.infrastructure.config", havingValue = "apollo", matchIfMissing = false)
@Service
public class ApolloDynamicConfig implements DynamicConfig {

    @Autowired
    private Environment environment;

    @Override
    public boolean getBoolean(String key, Boolean value) {
        return Boolean.valueOf(environment.getProperty(key, value.toString()));
    }

    @Override
    public int getInt(String key, Object value) {
        return Integer.valueOf(environment.getProperty(key, value + ""));
    }

    @Override
    public long getLong(String key, Object value) {
        return Long.valueOf(environment.getProperty(key, value + ""));
    }

    @Override
    public String getString(String key, String value) {
        return String.valueOf(environment.getProperty(key, value + ""));
    }
}