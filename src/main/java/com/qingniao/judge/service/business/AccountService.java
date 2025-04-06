package com.qingniao.judge.service.business;

import com.qingniao.judge.entity.User;

public interface AccountService {
    String login(String email, String password, boolean remember);
    void register(User user);
    void resetPassword(String email, String newPassword);
    boolean checkUsername(String username);
    boolean checkEmail(String email);
}
