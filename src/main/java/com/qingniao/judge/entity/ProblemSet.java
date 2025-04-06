package com.qingniao.judge.entity;

import com.qingniao.judge.enums.PSetStatus;
import lombok.Data;

import java.util.Date;

@Data
public class ProblemSet {
    private String id;
    private String name;
    private String introduction;
    private Date createTime;
    private Date updateTime;
    private String creatorID;
    private PSetStatus status;
}
