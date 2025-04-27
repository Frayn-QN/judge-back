package com.qingniao.judge.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.qingniao.judge.enums.TaskStatus;
import lombok.Data;

import java.util.Date;

@Data
public class TaskDisplay {
    private String id;
    private String problemID;
    private Integer serial;
    private String problemName;
    private Date judgeTime;
    private JsonNode answer;
    private TaskStatus status;
    private Float score;
    private JsonNode result;

    public TaskDisplay() {}
    public TaskDisplay(Task task, Problem problem) {
        if(!task.getProblemID().equals(problem.getId())) return;

        this.id = task.getId();
        this.problemID = task.getProblemID();
        this.serial = problem.getSerial();
        this.problemName = problem.getName();
        this.judgeTime = task.getJudgeTime();
        this.answer = task.getAnswer();
        this.status = task.getStatus();
        this.score = task.getScore();
        this.result = task.getResult();
    }
}
