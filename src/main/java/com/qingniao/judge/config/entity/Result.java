package com.qingniao.judge.config.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.Date;

@Data
@JsonSerialize
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;
    private Long timestamp;

    public Result() {
        this.timestamp = new Date().getTime();
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ReturnCode.RC_200.getCode());
        result.setMsg(ReturnCode.RC_200.getMsg());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> failure(int code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }
}
