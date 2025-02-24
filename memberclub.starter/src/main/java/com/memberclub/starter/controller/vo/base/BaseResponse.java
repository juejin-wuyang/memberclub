package com.memberclub.starter.controller.vo.base;

import lombok.Data;

@Data
public class BaseResponse {

    public boolean succ;

    public int errorCode;

    private String errorMsg;
}
