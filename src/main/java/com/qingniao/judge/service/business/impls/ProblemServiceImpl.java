package com.qingniao.judge.service.business.impls;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qingniao.judge.entity.Problem;
import com.qingniao.judge.enums.UserAuth;
import com.qingniao.judge.mapper.PSets_ProblemsMapper;
import com.qingniao.judge.mapper.ProblemMapper;
import com.qingniao.judge.mapper.UserMapper;
import com.qingniao.judge.service.business.ProblemService;
import com.qingniao.judge.config.entity.BusinessException;
import com.qingniao.judge.config.entity.ReturnCode;
import com.qingniao.judge.util.UUIDUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class ProblemServiceImpl implements ProblemService {
    private UserMapper userMapper;
    private ProblemMapper problemMapper;
    private PSets_ProblemsMapper psets_problemsMapper;

    private void verify(String userID, String problemID) {
        // 检查用户权限
        UserAuth auth = userMapper.selectAuthority(userID);
        switch (auth) {
            case USER -> throw new BusinessException("Problem", ReturnCode.RC_403);
            case COACH -> {
                if(problemID == null)return;
                // 检查是否为创建者
                Problem problem = problemMapper.selectOne(problemID);
                if(!problem.getCreatorID().equals(userID))
                    throw new BusinessException("Problem", ReturnCode.RC_403);
            }
        }
    }

    @Override
    public void addProblem(String userID, Problem problem) {
        verify(userID, null);

        problem.setId(UUIDUtil.generateUUIDStr());
        problem.setCreatorID(userID);
        problem.setCreateTime(new Date());

        // TODO: 执行一次评测验证题目

        problemMapper.insert(problem);
    }

    @Override
    public void modifyProblem(String userID, Problem problem) {
        verify(userID, problem.getId());

        problemMapper.update(problem);
    }

    @Override
    public void deleteProblem(String userID, String problemID) {
        verify(userID, problemID);

        problemMapper.delete(problemID);
        psets_problemsMapper.deleteByProblem(problemID);
    }

    @Override
    public Problem getProblem(String problemID) {
        // 所有人都能获取，用作题目详情展示
        return problemMapper.selectDisplay(problemID);
    }

    @Override
    public Problem getProblemDetail(String userID, String problemID) {
        verify(userID, problemID);

        return problemMapper.selectOne(problemID);
    }

    @Override
    public PageInfo<Problem> loadProblem(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(problemMapper.selectList());
    }

    @Override
    public PageInfo<Problem> loadUploaded(String userID, int pageNum, int pageSize) {
        UserAuth auth = userMapper.selectAuthority(userID);
        switch (auth) {
            case USER -> throw new BusinessException("Problem", ReturnCode.RC_403);
            case COACH -> {
                PageHelper.startPage(pageNum, pageSize);
                return new PageInfo<>(problemMapper.selectByUser(userID));
            }
            case ADMIN -> {
                PageHelper.startPage(pageNum, pageSize);
                return new PageInfo<>(problemMapper.selectList());
            }
        }
        return null;
    }

    @Override
    public PageInfo<Problem> searchProblem(String keyword, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(problemMapper.selectByKeyword(keyword));
    }
}
