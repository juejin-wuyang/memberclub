package com.memberclub.infrastructure.payment.context;

import lombok.Data;

@Data
public class PrePayResponseDTO {

    private int code;


    private String msg;
    /**
     * 跳转 url
     */
    private String url;

    private int payAmountFen;

    private String tradeNo;

    private String payToken;

    public boolean isSuccess() {
        return code == 0;
    }

}
