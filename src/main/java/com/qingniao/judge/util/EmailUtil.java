package com.qingniao.judge.util;

import com.qingniao.judge.config.entity.BusinessException;
import com.qingniao.judge.config.entity.ReturnCode;
import com.qingniao.judge.service.redis.RedisAccess;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class EmailUtil {
    private JavaMailSender mailSender;
    private RedisAccess redisAccess;

    public void sendAndSaveCode(String email) {
        String inputEmail = "verify_"+ email;
        String code = TokenUtil.generateVerifyCode(email);
        if(!redisAccess.hasKey(inputEmail)) {
            String inputCode = "code_"+ code;
            redisAccess.set(inputEmail, inputCode, 10, TimeUnit.MINUTES);
        }
        else {// 已存在验证码，需要重新发送
            String outputCode = (String) redisAccess.get(inputEmail);
            code = outputCode.substring(5);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("青鸟测评系统验证码");
        message.setText("您的验证码是："+ code+ "；有效期为十分钟。");
        mailSender.send(message);
    }

    public void verify(String email, String code) {// 验证码错误直接抛异常
        String inputEmail = "verify_"+ email;
        String outputCode = (String) redisAccess.get(inputEmail);

        if(!code.equals(outputCode.substring(5)))
            throw new BusinessException("Email", 400, "验证码错误");
    }
}
