package com.memberclub.starter.controller.vo.purchase;

import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewResponse;
import lombok.Data;

import java.util.List;

@Data
public class BuyRecordVO {

    private String tradeId;

    private Integer bizType;

    private List<BuySubOrderVO> subOrders;

    private String status;

    private AfterSalePreviewResponse previewResponse;
}
