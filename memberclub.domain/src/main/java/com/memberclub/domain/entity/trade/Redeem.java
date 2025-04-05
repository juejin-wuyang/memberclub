package com.memberclub.domain.entity.trade;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Redeem {
    @TableId(type = IdType.AUTO)
    private Long id;

    private int bizType;

    private long userId;

    private String code;

    private String relatedId;

    private int status;

    private long ctime;

    private long utime;
}
