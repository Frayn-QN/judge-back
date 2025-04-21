package com.qingniao.judge.service.task;

import com.qingniao.judge.entity.Problem;
import com.qingniao.judge.entity.Task;
import com.qingniao.judge.enums.TaskStatus;
import com.qingniao.judge.entity.TaskData;
import com.qingniao.judge.mapper.ProblemMapper;
import com.qingniao.judge.mapper.TaskMapper;
import com.qingniao.judge.service.task.ExecuteService;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CompileService {
    private TaskMapper taskMapper;
    private ProblemMapper problemMapper;

    private Queue CompileQueueInput;
    private RabbitTemplate rabbitTemplate;

    private ExecuteService executeService;

    @Async
    public void generateTaskData(Task task) {
        task.setStatus(TaskStatus.COMPILING);
        Problem problem = problemMapper.selectJudge(task.getProblemID());
        TaskData taskData = new TaskData();
        taskData.setTask(task);
        taskData.setExtra(problem.getExtra());

        this.pushTask(taskData);
    }

    private void pushTask(TaskData taskData) {
        rabbitTemplate.convertAndSend(CompileQueueInput.getName(), taskData);
    }

    @RabbitListener(queues = "CompileQueueOutput")
    private void pullTask(TaskData taskData) {
        // 在编译模块修改状态，只需数据通信
        Task task = taskData.getTask();
        taskMapper.update(task);
        //TODO websocket向前端同步编译信息

        if(task.getStatus().getCode() < -1) // 编译出现异常
            return;

        executeService.dealTaskData(task);
    }
}
