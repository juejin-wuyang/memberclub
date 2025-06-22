package com.memberclub.infrastructure.amountcompute.spi;

import com.memberclub.infrastructure.amountcompute.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnProperty(name = "memberclub.infrastructure.amountcompute", havingValue = "local")
public class MockAmountComputeSPI implements AmountComputeSPI {

    @Override
    public AmountComputeResponseDTO compute(AmountComputeRequestDTO amountComputeRequestDTO) {
        AmountComputeResponseDTO response = new AmountComputeResponseDTO();

        int salePriceFenSum = 0;
        List<AmountComputeProductDetailResult> results = new ArrayList<>();
        for (AmountComputeProductDetailDTO detail : amountComputeRequestDTO.getProductDetails()) {
            AmountComputeProductDetailResult result = new AmountComputeProductDetailResult();
            result.setSkuId(detail.getSkuId());
            result.setAmountFen(detail.getSalePriceFen() * detail.getCount() - 50);
            salePriceFenSum += result.getAmountFen();
            results.add(result);
        }

        response.setTotalAmountFen(salePriceFenSum);
        response.setDetails(results);
        response.setCode(0);
        return response;
    }
}
