package com.memberclub.starter.controller.vo.sku;

import lombok.Data;

@Data
public class SkuPreviewVO {

    private String image;

    private Long id;

    private String title;

    private String desc;

    private String attr_val;

    private String originPrice;

    private String price;

    private Long stock;

    private boolean singleBuy;

    private int bizId;

    private int firmId;

    private String firmName;
}
