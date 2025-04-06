package com.qingniao.judge.entity;

import com.qingniao.judge.enums.UserAuth;
import lombok.Data;

import java.util.Date;

@Data
public class User {
    private String id;
    private String email;
    private UserAuth authority;
    private String username;
    private String password;
    private byte[] avatar;
    private String introduction;
    private Date registerTime;
}