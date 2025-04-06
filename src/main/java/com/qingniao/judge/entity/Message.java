package com.qingniao.judge.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Message {
    private String id;
    private String sendID;
    private String receiveID;
    private String receiveUsername;
    private Date sendTime;
    private String title;
    private String content;
}
