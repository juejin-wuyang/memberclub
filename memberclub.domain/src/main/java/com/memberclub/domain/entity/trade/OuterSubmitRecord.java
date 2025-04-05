/**
 * @(#)OuterSubmitRecord.java, 四月 05, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.entity.trade;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author: 掘金五阳
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OuterSubmitRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private long userId;

    /**
     * 外部下单 ID
     */
    private String outerId;

    /**
     * 外部下单配置 iD
     */
    private String outerConfigId;

    private String tradeId;

    private int bizType;

    private String extra;

    /**
     * 外部下单类型
     */
    private int outerType;

    private int status;

    private long utime;

    private long ctime;
}