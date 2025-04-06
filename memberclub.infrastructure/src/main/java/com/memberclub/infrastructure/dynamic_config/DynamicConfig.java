/**
 * @(#)DynamicConfig.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.dynamic_config;

/**
 * // TODO: 2024/12/14 接入分布式字典服务
 *
 * @author 掘金五阳
 */
public interface DynamicConfig {

    boolean getBoolean(String key, Boolean value);

    int getInt(String key, Object value);

    long getLong(String key, Object value);

    String getString(String key, String value);
}