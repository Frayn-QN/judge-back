package com.qingniao.judge.mapper;

import com.qingniao.judge.entity.Problem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProblemMapper {
    void insert(Problem problem);
    void delete(String id);
    void update(Problem problem);
    Problem selectOne(String id);
    List<Problem> selectList();
    List<Problem> selectByUser(String creatorID);
    Problem selectDisplay(String id);
    Problem selectJudge(String id);
    List<Problem> selectByKeyword(String keyword);
}
