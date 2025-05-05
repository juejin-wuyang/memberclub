package com.memberclub.infrastructure.amountcompute;

import lombok.Data;

import java.util.List;

@Data
public class AmountComputeResponseDTO {

    private int code;
    /**
     * 折扣后价格
     */
    private int totalAmountFen;

    private List<AmountComputeProductDetailResult> details;


    public boolean isSuccess() {
        return code == 0;
    }

}
