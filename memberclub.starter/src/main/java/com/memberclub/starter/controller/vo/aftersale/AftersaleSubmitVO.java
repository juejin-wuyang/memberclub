package com.memberclub.starter.controller.vo.aftersale;

import lombok.Data;

@Data
public class AftersaleSubmitVO {

    private Integer bizType;

    private String tradeId;

    private int source;

    private String previewDigest;

    private Integer digestVersion;
}
