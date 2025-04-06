package com.qingniao.judge.service.business;

import com.github.pagehelper.PageInfo;
import com.qingniao.judge.entity.Problem;

public interface ProblemService {
    void addProblem(String userID, Problem problem);
    void modifyProblem(String userID, Problem problem);
    void deleteProblem(String userID, String problemID);
    Problem getProblem(String problemID);
    PageInfo<Problem> loadProblem(int pageNum, int pageSize);
    PageInfo<Problem> loadUploaded(String userID, int pageNum, int pageSize);
    PageInfo<Problem> searchProblem(String keyword, int pageNum, int pageSize);
}
