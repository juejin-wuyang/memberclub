package com.memberclub.domain.entity.trade;

import lombok.Data;

@Data
public class Redeem {

    private long id;

    private int bizType;

    private long userId;

    private String code;

    private String relatedId;

    private int status;

    private long ctime;

    private long utime;
}
