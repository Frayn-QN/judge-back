package com.qingniao.judge.service.business;

import com.github.pagehelper.PageInfo;
import com.qingniao.judge.entity.Task;
import com.qingniao.judge.entity.TaskDisplay;

import java.util.Map;

public interface TaskService {
    /**
     * 新建任务、开始执行流程、返回id
     * @param userID
     * @param task
     * @return taskID
     */
    String addTask(String userID, Task task);
    // TODO: user test
    void deleteTask(String userID, String taskID);
    TaskDisplay getTask(String userID, String taskID);
    Map<String, Object> getUserStatistic(String userID);
    Map<String, Object> getProblemStatistic(String problemID);
    PageInfo<Task> loadByUser(String userID, int pageNum, int pageSize);
}
