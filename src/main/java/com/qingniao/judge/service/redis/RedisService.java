package com.qingniao.judge.service.redis;

import com.qingniao.judge.enums.BanTime;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;

public interface RedisService {
    String queryUserID(HttpServletRequest request);
    String setToken(String userID, boolean remember);
    Boolean hasToken(String token);
    void refreshToken(String token);
    void removeToken(String token);
    void banUser(String userID, BanTime banTime);
    Date getBanTime(String userID);
}
