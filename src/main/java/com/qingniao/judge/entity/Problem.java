package com.qingniao.judge.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.qingniao.judge.enums.ProblemLevel;
import lombok.Data;

import java.util.Date;

@Data
public class Problem {
    private String id;
    private Integer serial;
    private String creatorID;
    private Date createTime;
    private String name;
    private ProblemLevel level;
    private String content;
    private JsonNode example;
    private JsonNode expectation;
    private JsonNode extra;
    private Integer timeLimit;
    private Integer memoryLimit;
}
