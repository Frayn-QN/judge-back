package com.qingniao.judge.service.business;

import com.github.pagehelper.PageInfo;
import com.qingniao.judge.entity.User;

public interface UserService {
    User getInfo(String userID);
    void changeInfo(User user);
    void unregister(String userID, String email);
    PageInfo<User> loadUser(int pageNum, int pageSize);
    PageInfo<User> searchUser(String keyword, int pageNum, int pageSize);
    void verifyAdmin(String userID, String targetID);
}
