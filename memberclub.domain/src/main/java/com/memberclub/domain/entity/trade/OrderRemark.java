package com.memberclub.domain.entity.trade;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class OrderRemark {

    @TableId(type = IdType.AUTO)
    private Long id;

    private int bizType;

    private long userId;

    private String tradeId;

    private String detail;

    private long utime;

    private long ctime;
}
