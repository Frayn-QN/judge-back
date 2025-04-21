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

    @Async
    public void dealTaskData(Task task) {
        TaskData taskData = new TaskData();
        Problem problem = problemMapper.selectJudge(taskData.getTask().getProblemID());

        taskData.setTask(task);
        taskData.setExample(problem.getExample());
        taskData.setExpectation(problem.getExpectation());
        taskData.setTimeLimit(problem.getTimeLimit());
        taskData.setMemoryLimit(problem.getMemoryLimit());

        this.pushTask(taskData);
    }

    private void pushTask(TaskData taskData) {
        rabbitTemplate.convertAndSend(ExecuteQueueInput.getName(), taskData);
    }

    @RabbitListener(queues = "ExecuteQueueOutput")
    private void pullTask(TaskData taskData) {
        Task task = taskData.getTask();
        taskMapper.update(task);
        //TODO websocket向前端同步执行结果

    }
}
