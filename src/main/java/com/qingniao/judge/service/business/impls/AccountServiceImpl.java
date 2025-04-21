package com.qingniao.judge.service.business.impls;

import com.qingniao.judge.config.entity.BusinessException;
import com.qingniao.judge.config.entity.ReturnCode;
import com.qingniao.judge.entity.ProblemSet;
import com.qingniao.judge.entity.User;
import com.qingniao.judge.enums.PSetStatus;
import com.qingniao.judge.enums.UserAuth;
import com.qingniao.judge.mapper.UserMapper;
import com.qingniao.judge.service.business.AccountService;
import com.qingniao.judge.service.business.ProblemSetService;
import com.qingniao.judge.service.redis.RedisService;
import com.qingniao.judge.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Date;
import java.util.Objects;

@Service
public class AccountServiceImpl implements AccountService {
    private UserMapper userMapper;
    private RedisService redisService;
    private PasswordUtil passwordUtil;
    private ProblemSetService psetService;

    @Autowired
    public AccountServiceImpl(UserMapper userMapper, RedisService redisService, PasswordUtil passwordUtil,
                              ProblemSetService psetService) {
        this.userMapper = userMapper;
        this.redisService = redisService;
        this.passwordUtil = passwordUtil;
        this.psetService = psetService;
    }

    @Value("${judge.default-avatar-base64}")
    private String defaultAvatarBase64Str;

    @Override
    @Transactional
    public String login(String email, String password, boolean remember) {
        User user = userMapper.selectByEmail(email);

        if(user == null)// 用户不存在
            throw new BusinessException("Account", 403, "用户不存在");

        if(!isPasswordCorrect(user, password))// 密码错误
            throw new BusinessException("Account", 403, "密码错误");

        Date banTime = redisService.getBanTime(email);
        if(banTime != null)// 用户被封禁
            throw new BusinessException("Account", 403, "封禁时间："+ banTime.toString());

        return redisService.setToken(user.getId(), remember);
    }

    @Override
    public void register(User user) {
        // 检验特殊字段
        if(Objects.equals(user.getUsername(), "ALL"))
            throw new BusinessException("Account", 403, "用户名使用特殊字段");

        user.setId(UUIDUtil.generateUUIDStr());
        user.setRegisterTime(new Date());
        user.setAuthority(UserAuth.USER);
        user.setIntroduction("Hello world!");
        user.setAvatar(Base64.getDecoder().decode(defaultAvatarBase64Str));

        userMapper.insert(user);

        // 创建默认题集（收藏夹）
        ProblemSet problemSet = new ProblemSet();
        problemSet.setName(user.getUsername()+ "的收藏夹");
        problemSet.setStatus(PSetStatus.PRIVATE);
        problemSet.setIntroduction(user.getUsername()+ "收藏题目");
        psetService.addPSet(user.getId(), problemSet);
    }

    @Override
    public void resetPassword(String email, String newPassword) {
        User user = userMapper.selectByEmail(email);
        user.setPassword(passwordUtil.dealPassword(newPassword));

        userMapper.update(user);
    }

    @Override
    public boolean checkUsername(String username) {
        return userMapper.selectByUsername(username) == null;
    }

    @Override
    public boolean checkEmail(String email) {
        return userMapper.selectByEmail(email) == null;
    }

    private boolean isPasswordCorrect(User user, String inputPwd) {
        return user.getPassword().equals(passwordUtil.dealPassword(inputPwd));
    }
}
