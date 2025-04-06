package com.qingniao.judge.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.qingniao.judge.entity.Task;
import lombok.Data;

@Data
public class TaskData {
    private Task task;
    private JsonNode extra;
    private JsonNode example;
    private JsonNode expectation;
    private JsonNode execute;
    private Integer testCount;
    private Integer timeLimit;  // 单位ms
    private Integer memoryLimit;// 单位MB
}
