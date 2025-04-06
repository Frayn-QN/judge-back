package com.qingniao.judge.mapper;

import com.qingniao.judge.entity.Problem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PSets_ProblemsMapper {
    void insert(String psetID, String problemID);
    void deleteByPSet(String psetID);
    void deleteByProblem(String problemID);
    void deleteByPSetAndProblem(String psetID, String problemID);
    List<Problem> selectByPSet(String psetID);
}
