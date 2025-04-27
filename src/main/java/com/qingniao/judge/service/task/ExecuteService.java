package com.qingniao.judge.service.task;

import com.qingniao.judge.entity.Problem;
import com.qingniao.judge.entity.Task;
import com.qingniao.judge.entity.TaskData;
import com.qingniao.judge.mapper.ProblemMapper;
import com.qingniao.judge.mapper.TaskMapper;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ExecuteService {
    private TaskMapper taskMapper;
    private ProblemMapper problemMapper;
    private Queue ExecuteQueueInput;
    private RabbitTemplate rabbitTemplate;
    private WebSocketService webSocketService;

    @Async
    public void dealAndPushTaskData(Task task) {
        TaskData taskData = new TaskData();
        Problem problem = problemMapper.selectJudge(task.getProblemID());

        taskData.setExecute(task.getResult());
        task.setResult(null);

        taskData.setTask(task);
        taskData.setExample(problem.getExample());
        taskData.setExpectation(problem.getExpectation());
        taskData.setTimeLimit(problem.getTimeLimit());
        taskData.setMemoryLimit(problem.getMemoryLimit());
        taskData.setTestCount(problem.getTestCount());

        rabbitTemplate.convertAndSend(ExecuteQueueInput.getName(), taskData);
    }

    @RabbitListener(queues = "ExecuteQueueOutput")
    public void pullTask(TaskData taskData) {
        Task task = taskData.getTask();
        if(isJudge(task)) taskMapper.update(task);
        // websocket向前端同步task
        webSocketService.sendTask(task);
    }

    private boolean isJudge(Task task) {
        if(task.getAnswer().get("judge") == null) return false;
        return task.getAnswer().get("judge").asBoolean();
    }
}
