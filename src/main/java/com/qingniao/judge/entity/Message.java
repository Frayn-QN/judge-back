package com.qingniao.judge.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Message {
    private String id;
    private String sendID;
    private String receiveID;
    private String sendName;
    private String receiveName;
    private Date sendTime;
    private String title;
    private String content;
}
