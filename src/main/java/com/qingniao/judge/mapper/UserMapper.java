package com.qingniao.judge.mapper;

import com.qingniao.judge.entity.User;
import com.qingniao.judge.enums.UserAuth;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    void insert(User user);
    void delete(String id);
    void update(User user);
    User selectOne(String id);
    List<User> selectList();
    UserAuth selectAuthority(String id);
    User selectByEmail(String email);
    String selectByUsername(String username);
    List<User> selectByKeyword(String keyword);
}
