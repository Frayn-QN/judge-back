package com.qingniao.judge.service.business;

import com.github.pagehelper.PageInfo;
import com.qingniao.judge.entity.Problem;
import com.qingniao.judge.entity.ProblemSet;
import com.qingniao.judge.entity.User;
import com.qingniao.judge.enums.PSets_UsersAuth;

import java.util.List;

public interface ProblemSetService {
    void addPSet(String userID, ProblemSet problemSet);
    void modifyPSet(String userID, ProblemSet problemSet);
    void deletePSet(String userID, String psetID);
    ProblemSet getPSet(String userID, String psetID);
    PageInfo<ProblemSet> loadPSet(int pageNum, int pageSize);
    PageInfo<ProblemSet> loadPsetByAuth(String userID, PSets_UsersAuth authority, int pageNum, int pageSize);
    void joinProblem(String userID, String psetID, String problemID);
    void removeProblem(String userID, String psetID, String problemID);
    PageInfo<Problem> loadPSetProblems(String userID, String psetID, int pageNum, int pageSize);
    List<User> loadPSetUsers(String userID, String psetID, PSets_UsersAuth authority);
    void joinUser(String userID, String psetID, String targetID, PSets_UsersAuth authority);
    void removeUser(String userID, String psetID, String targetID);
    void changeUserAuth(String userID, String psetID, String targetID, PSets_UsersAuth newAuth);
    PSets_UsersAuth getUserAuth(String userID, String psetID);
    void clearByUser(String userID);
    PageInfo<ProblemSet> searchPSet(String keyword, int pageNum, int pageSize);
}
