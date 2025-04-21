package com.qingniao.judge.mapper;

import com.qingniao.judge.entity.ProblemSet;
import com.qingniao.judge.entity.User;
import com.qingniao.judge.enums.PSets_UsersAuth;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PSets_UsersMapper {
    void insert(String psetID, String userID, PSets_UsersAuth authority);
    void deleteByPSet(String psetID);
    void deleteByUser(String userID);
    void deleteByPSetAndUser(String psetID, String userID);
    void updateUserAuth(String psetID, String userID, PSets_UsersAuth newAuth);

    /**
     * 获取题目集中不同权限的用户
     * @param psetID
     * @param authority 题集权限
     * @return 用户列表
     */
    List<User> selectByPSetAndAuth(String psetID, PSets_UsersAuth authority);

    /**
     * 获取用户不同权限的题目集
     * @param userID
     * @param authority 题集权限
     * @return 题目集列表
     */
    List<ProblemSet> selectByUserAndAuth(String userID, PSets_UsersAuth authority);

    /**
     * 获取题目集内某用户的权限
     * @param psetID
     * @param userID
     * @return 题集权限列表
     */
    PSets_UsersAuth selectByPSetAndUser(String psetID, String userID);
}
