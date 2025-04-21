package com.qingniao.judge.service.business.impls;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qingniao.judge.config.entity.BusinessException;
import com.qingniao.judge.config.entity.ReturnCode;

import com.qingniao.judge.entity.User;
import com.qingniao.judge.enums.UserAuth;
import com.qingniao.judge.mapper.TaskMapper;
import com.qingniao.judge.mapper.UserMapper;
import com.qingniao.judge.service.business.ProblemSetService;
import com.qingniao.judge.service.business.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserMapper userMapper;
    private TaskMapper taskMapper;

    private ProblemSetService psetService;

    @Override
    public User getInfo(String userID) {

        return userMapper.selectOne(userID);
    }

    @Override
    public void changeInfo(User user) {
        userMapper.update(user);
    }

    @Override
    public void unregister(String userID, String email) {
        String storedEmail = userMapper.selectOne(userID).getEmail();
        if(!email.equals(storedEmail))
            throw new BusinessException("User", ReturnCode.RC_401);

        userMapper.delete(userID);
        psetService.clearByUser(userID);
        taskMapper.deleteByUser(userID);
    }

    @Override
    public PageInfo<User> loadUser(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(userMapper.selectList());
    }

    @Override
    public PageInfo<User> searchUser(String keyword, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(userMapper.selectByKeyword(keyword));
    }

    @Override
    public void verifyAdmin(String userID, String targetID) {
        // 只允许admin改非admin
        if(!userMapper.selectAuthority(userID).equals(UserAuth.ADMIN))
            throw new BusinessException("User", 403, "权限不足");
        if(userMapper.selectAuthority(targetID).equals(UserAuth.ADMIN))
            throw new BusinessException("User", 403, "不允许操作管理员");
    }
}
