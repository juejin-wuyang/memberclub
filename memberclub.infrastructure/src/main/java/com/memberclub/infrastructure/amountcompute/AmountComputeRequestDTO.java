package com.memberclub.infrastructure.amountcompute;

import lombok.Data;

import java.util.List;

@Data
public class AmountComputeRequestDTO {

    private long userId;

    private String uuid;

    private String phone;

    private String uniqueId;

    private boolean submitRequest;

    private List<AmountComputeProductDetailDTO> productDetails;
}
