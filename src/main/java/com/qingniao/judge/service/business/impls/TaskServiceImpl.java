package com.qingniao.judge.service.business.impls;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qingniao.judge.entity.Task;
import com.qingniao.judge.entity.TaskDisplay;
import com.qingniao.judge.enums.TaskStatus;
import com.qingniao.judge.mapper.TaskMapper;
import com.qingniao.judge.service.business.TaskService;
import com.qingniao.judge.service.task.CompileService;
import com.qingniao.judge.config.entity.BusinessException;
import com.qingniao.judge.config.entity.ReturnCode;
import com.qingniao.judge.util.UUIDUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {
    private TaskMapper taskMapper;

    private CompileService compileService;

    private void verify(String userID, String taskID) {
        Task task = taskMapper.selectOne(taskID);
        if(!task.getUserID().equals(userID))
            throw new BusinessException("Task", ReturnCode.RC_403);
    }

    @Override
    public String addTask(String userID, Task task) {
        task.setId(UUIDUtil.generateUUIDStr());
        task.setUserID(userID);
        task.setJudgeTime(new Date());
        task.setScore(0f);
        task.setStatus(TaskStatus.COMPILING);

        // 异步执行任务
        compileService.generateAndPushTaskData(task);

        return task.getId();
    }

    @Override
    public void deleteTask(String userID, String taskID) {
        verify(userID, taskID);

        taskMapper.delete(taskID);
    }

    @Override
    public TaskDisplay getTask(String userID, String taskID) {
        verify(userID, taskID);

        return taskMapper.selectDisplayOne(taskID);
    }

    @Override
    public Map<String, Object> getUserStatistic(String userID) {
        List<Task> taskList = taskMapper.selectByUser(userID);
        Map<String, Object> resultMap = new HashMap<>();

        int count_AC = 0, count_WA = 0, count_ERR = 0, count_total = 0;
        float scoreSum = 0, scoreAverage = 0;
        for (Task task : taskList) {
            switch (task.getStatus()) {
                case AC -> count_AC++;
                case WA -> count_WA++;
                case COMPILING, EXECUTING -> {continue;}
                default -> count_ERR++;
            }
            count_total++;
            scoreSum += task.getScore();
        }

        if(count_total != 0) scoreAverage = scoreSum / count_total;

        resultMap.put("total", count_total);
        resultMap.put("average", scoreAverage);
        resultMap.put("AC", count_AC);
        resultMap.put("WA", count_WA);
        resultMap.put("ERR", count_ERR);

        return resultMap;
    }

    @Override
    public Map<String, Object> getProblemStatistic(String problemID) {
        List<Task> taskList = taskMapper.selectByProblem(problemID);
        Map<String, Object> resultMap = new HashMap<>();

        int count_AC = 0, count_WA = 0, count_ERR = 0, count_total = 0;
        float scoreSum = 0, scoreAverage = 0;
        for (Task task : taskList) {
            switch (task.getStatus()) {
                case AC -> count_AC++;
                case WA -> count_WA++;
                case COMPILING, EXECUTING -> {continue;}
                default -> count_ERR++;
            }
            count_total++;
            scoreSum += task.getScore();
        }
        if(count_total != 0) scoreAverage = scoreSum / count_total;

        resultMap.put("total", count_total);
        resultMap.put("average", scoreAverage);
        resultMap.put("AC", count_AC);
        resultMap.put("WA", count_WA);
        resultMap.put("ERR", count_ERR);

        return resultMap;
    }

    @Override
    public PageInfo<Task> loadByUser(String userID, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(taskMapper.selectDisplayList(userID));
    }
}
