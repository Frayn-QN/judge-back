package com.qingniao.judge.controller;

import com.github.pagehelper.PageInfo;
import com.qingniao.judge.entity.Task;
import com.qingniao.judge.service.business.TaskService;
import com.qingniao.judge.service.redis.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/task")
@AllArgsConstructor
public class TaskController {
    private TaskService taskService;
    private RedisService redisService;

    @PostMapping("/add")
    void add(HttpServletRequest request, @RequestBody Task task) {
        String userID = redisService.queryUserID(request);
        taskService.addTask(userID, task);
    }

    @GetMapping("/delete")
    void delete(HttpServletRequest request, @RequestParam String id) {
        String userID = redisService.queryUserID(request);
        taskService.deleteTask(userID, id);
    }

    @GetMapping("/get")
    Task get(HttpServletRequest request, @RequestParam String id) {
        String userID = redisService.queryUserID(request);
        return taskService.getTask(userID, id);
    }

    @GetMapping("/count/problem")
    Map<String, Object> countByProblem(@RequestParam String id) {
        return taskService.getProblemStatistic(id);
    }

    @GetMapping("/count/user")
    Map<String, Object> countByUser(HttpServletRequest request) {
        String userID = redisService.queryUserID(request);
        return taskService.getUserStatistic(userID);
    }

    @GetMapping("/history")
    PageInfo<Task> getHistory(HttpServletRequest request, @RequestParam int pageNum, @RequestParam int pageSize) {
        String userID = redisService.queryUserID(request);
        return taskService.loadByUser(userID, pageNum, pageSize);
    }
}
