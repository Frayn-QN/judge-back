package com.qingniao.judge.mapper;

import com.qingniao.judge.entity.ProblemSet;
import com.qingniao.judge.enums.PSetStatus;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProblemSetMapper {
    void insert(ProblemSet problemSet);
    void delete(String id);
    void update(ProblemSet problemSet);
    ProblemSet selectOne(String id);
    List<ProblemSet> selectList();
    PSetStatus selectStatus(String id);
    List<String> selectByUser(String creatorID);
    List<ProblemSet> selectByKeyword(String keyword);
}
