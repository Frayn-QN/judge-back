package com.qingniao.judge.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.qingniao.judge.entity.User;
import com.qingniao.judge.service.business.AccountService;
import com.qingniao.judge.util.EmailUtil;
import com.qingniao.judge.util.PasswordUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@AllArgsConstructor
public class AccountController {
    private AccountService accountService;
    private PasswordUtil passwordUtil;
    private EmailUtil emailUtil;

    @PostMapping("/login")
    String login(@RequestBody JsonNode body) {
        String email = body.get("email").asText();
        String password = body.get("password").asText();
        boolean remember = body.get("remember").asBoolean();

        return accountService.login(email, password, remember);
    }

    @PostMapping("/register")
    void register(@RequestBody User user, @RequestParam String code) {
        emailUtil.verify(user.getEmail(), code);

        accountService.register(user);
    }

    @PostMapping("/reset")
    void resetPassword(@RequestBody User user, @RequestParam String code) {
        emailUtil.verify(user.getEmail(), code);

        accountService.resetPassword(user.getEmail(), user.getPassword());
    }

    @GetMapping("/key")
    String getPublicKeyStr() {
        return passwordUtil.getPublicKeyStr();
    }

    @GetMapping("/verify")
    void sendVerification(@RequestParam String email) {
        emailUtil.sendAndSaveCode(email);
    }

    @GetMapping("/check")
    boolean check(@RequestParam(required = false) String username,
                  @RequestParam(required = false) String email) {
        if(username != null)
            return accountService.checkUsername(username);
        if(email != null)
            return accountService.checkEmail(email);
        return false;
    }
}
