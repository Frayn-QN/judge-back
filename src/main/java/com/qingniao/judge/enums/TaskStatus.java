package com.qingniao.judge.enums;

import lombok.Getter;

@Getter
public enum TaskStatus {
    COMPILING(2),
    EXECUTING(1),
    AC(0),      // accept
    WA(-1),     // wrong answer
    UKE(-2),    // unknown error
    RE(-3),     // runtime error
    CE(-4),     // compile error
    TLE(-5),    // time limit error
    MLE(-6),    // memory limit error
    OLE(-7);    // output limit error

    private final int code;
    TaskStatus(int code) {
        this.code = code;
    }
}
