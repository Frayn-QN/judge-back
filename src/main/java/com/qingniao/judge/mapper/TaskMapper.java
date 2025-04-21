package com.qingniao.judge.mapper;

import com.qingniao.judge.entity.Task;
import com.qingniao.judge.entity.TaskDisplay;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskMapper {
    void insert(Task task);
    void delete(String id);
    void deleteByUser(String userID);
    void update(Task task);
    Task selectOne(String id);
    List<Task> selectByUser(String userID);
    List<Task> selectByProblem(String problemID);
    List<Task> selectDisplayList(String userID);
    TaskDisplay selectDisplayOne(String userID);
}
