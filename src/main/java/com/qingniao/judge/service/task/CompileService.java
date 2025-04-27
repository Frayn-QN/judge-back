package com.qingniao.judge.service.task;

import com.qingniao.judge.entity.Problem;
import com.qingniao.judge.entity.Task;
import com.qingniao.judge.entity.TaskDisplay;
import com.qingniao.judge.enums.TaskStatus;
import com.qingniao.judge.entity.TaskData;
import com.qingniao.judge.mapper.ProblemMapper;
import com.qingniao.judge.mapper.TaskMapper;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

@Service
@AllArgsConstructor
public class CompileService {
    private TaskMapper taskMapper;
    private ProblemMapper problemMapper;
    private Queue CompileQueueInput;
    private RabbitTemplate rabbitTemplate;
    private WebSocketService webSocketService;

    private ExecuteService executeService;

    @Async
    public void generateAndPushTaskData(Task task) {
        task.setStatus(TaskStatus.COMPILING);
        if(isJudge(task)) taskMapper.insert(task);
        Problem problem = problemMapper.selectJudge(task.getProblemID());

        // websocket向前端同步task，初始需要taskDisplay获取题目信息
        TaskDisplay taskDisplay = new TaskDisplay(task, problem);
        webSocketService.sendTask(taskDisplay);

        // 处理TaskData
        TaskData taskData = new TaskData();
        taskData.setTask(task);
        taskData.setExtra(problem.getExtra());

        rabbitTemplate.convertAndSend(CompileQueueInput.getName(), taskData);
    }

    @RabbitListener(queues = "CompileQueueOutput")
    public void pullTaskData(TaskData taskData) {
        Task task = taskData.getTask();
        if(task.getStatus().getCode() >= 0) // 编译通过
        {
            task.setStatus(TaskStatus.EXECUTING);
            Task task_copy = SerializationUtils.clone(task);
            executeService.dealAndPushTaskData(task_copy); // async
            task.setResult(null);
        }
        if(isJudge(task)) taskMapper.update(task);// DB
        // websocket向前端同步task
        webSocketService.sendTask(task);

    }

    private boolean isJudge(Task task) {
        if(task.getAnswer().get("judge") == null) return false;
        return task.getAnswer().get("judge").asBoolean();
    }
}
