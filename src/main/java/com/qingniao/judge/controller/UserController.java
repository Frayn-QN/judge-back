package com.qingniao.judge.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.pagehelper.PageInfo;
import com.qingniao.judge.entity.User;
import com.qingniao.judge.enums.BanTime;
import com.qingniao.judge.service.business.UserService;
import com.qingniao.judge.service.redis.RedisService;
import com.qingniao.judge.util.EmailUtil;
import com.qingniao.judge.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private UserService userService;
    private RedisService redisService;
    private EmailUtil emailUtil;

    @GetMapping("/logout")
    void logout(HttpServletRequest request) {
        redisService.removeToken(TokenUtil.extractToken(request));
    }

    @GetMapping("/getInfo")
    User getInfo(HttpServletRequest request) {
        String userID = redisService.queryUserID(request);
        return userService.getInfo(userID);
    }

    @PostMapping("/changeInfo")
    void changeInfo(HttpServletRequest request, @RequestBody User user) {
        user.setId(redisService.queryUserID(request));
        userService.changeInfo(user);
    }

    @GetMapping("/unregister")
    void unregister(HttpServletRequest request, @RequestBody JsonNode body) {
        String email = body.get("email").toString();
        String code = body.get("code").toString();

        emailUtil.verify(email, code);
        String userID = redisService.queryUserID(request);
        userService.unregister(userID, email);
    }

    /**
     * 管理员修改用户权限
     * @param request
     * @param user 包含targetID、authority
     */
    @PostMapping("/auth")
    void changeUserAuth(HttpServletRequest request, @RequestBody User user) {
        String userID = redisService.queryUserID(request);
        String targetID = user.getId();
        userService.verifyAdmin(userID, targetID);

        userService.changeInfo(user);
    }

    @PostMapping("/ban")
    void banUser(HttpServletRequest request, @RequestBody JsonNode body) {
        String userID = redisService.queryUserID(request);
        String targetID = body.get("targetID").toString();
        userService.verifyAdmin(userID, targetID);

        String banTimeStr = body.get("ban_time").toString();
        redisService.banUser(targetID, BanTime.fromDescription(banTimeStr));
    }

    @GetMapping("/load")
    PageInfo<User> loadUser(@RequestParam int pageNum, @RequestParam int pageSize) {
        return userService.loadUser(pageNum, pageSize);
    }

    @GetMapping("/search")
    PageInfo<User> searchUser(@RequestParam String keyword,
                                     @RequestParam int pageNum, @RequestParam int pageSize) {
        return userService.searchUser(keyword, pageNum, pageSize);
    }
}
