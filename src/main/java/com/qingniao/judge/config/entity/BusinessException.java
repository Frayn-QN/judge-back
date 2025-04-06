package com.qingniao.judge.config.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BusinessException extends RuntimeException{
    private Integer code;
    private String message;
    private String service;

    public BusinessException(String service, ReturnCode returnCode) {
        this(service, returnCode.getCode(), returnCode.getMsg());
    }

    public BusinessException(String service, Exception e) {
        super(e);
        this.service = service;
        this.code = ReturnCode.RC_500.getCode();
        this.message = e.getMessage();
    }

    public BusinessException(String service, int code, String message) {
        super(message);
        this.service = service;
        this.code = code;
        this.message = message;
    }
}
