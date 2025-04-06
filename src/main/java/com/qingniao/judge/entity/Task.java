package com.qingniao.judge.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.qingniao.judge.enums.TaskStatus;
import lombok.Data;

import java.util.Date;

@Data
public class Task {
    private String id;
    private String problemID;
    private String userID;
    private Date judgeTime;
    private JsonNode answer;
    private TaskStatus status;
    private Float score;
    private JsonNode result;
}
