package com.memberclub.starter.controller.vo.base;

import lombok.Data;

@Data
public class DataResponse<T> extends BaseResponse {

    private T data;
}
