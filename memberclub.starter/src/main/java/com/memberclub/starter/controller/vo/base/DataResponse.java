package com.memberclub.starter.controller.vo.base;

import lombok.Data;

@Data
public class DataResponse<T> extends BaseResponse {

    private T data;

    public static <T> DataResponse<T> success(T t) {
        DataResponse<T> response = new DataResponse<T>();
        response.setSucc(true);
        response.setData(t);
        return response;
    }
}
