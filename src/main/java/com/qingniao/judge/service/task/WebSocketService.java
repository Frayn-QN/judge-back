package com.qingniao.judge.service.task;

import com.qingniao.judge.entity.Task;
import com.qingniao.judge.entity.TaskDisplay;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendTask (Task task) {
        // 发送到统一主题，消息体携带任务ID和完整状态
        String router = "/topic/task/" + task.getId();
        messagingTemplate.convertAndSend(router, task);
    }

    public void sendTask (TaskDisplay taskDisplay) {
        // 发送到统一主题，消息体携带任务ID和完整状态
        String router = "/topic/task/" + taskDisplay.getId();
        messagingTemplate.convertAndSend(router, taskDisplay);
    }
}