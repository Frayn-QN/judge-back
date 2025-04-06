package com.qingniao.judge.config.entity;

import lombok.Getter;

@Getter
public enum ReturnCode {
    RC_200(200, "OK"),
    RC_400(400, "客户端错误"),
    RC_401(401, "认证失败"),// 与用户登录失效强关联
    RC_403(403, "拒绝访问"),
    RC_404(404, "找不到"),
    RC_405(405, "请求方式错误"),
    RC_500(500, "服务器错误");

    private final int code;
    private final String msg;

    ReturnCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
