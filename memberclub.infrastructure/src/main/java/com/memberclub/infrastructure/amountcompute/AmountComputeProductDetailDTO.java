package com.memberclub.infrastructure.amountcompute;

import lombok.Data;

@Data
public class AmountComputeProductDetailDTO {

    private long skuId;

    private int count;

    private int originPrice;

    private int salePriceFen;
}
