package com.qingniao.judge.controller;

import com.github.pagehelper.PageInfo;
import com.qingniao.judge.entity.Message;
import com.qingniao.judge.service.business.MessageService;
import com.qingniao.judge.service.redis.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
@AllArgsConstructor
public class MessageController {
    private MessageService messageService;
    private RedisService redisService;

    @PostMapping("/add")
    void add(HttpServletRequest request, @RequestBody Message message) {
        String userID = redisService.queryUserID(request);
        messageService.addMessage(userID, message);
    }

    @GetMapping("/delete")
    void delete(HttpServletRequest request, @RequestParam String id) {
        String userID = redisService.queryUserID(request);
        messageService.deleteMessage(userID, id);
    }

    @GetMapping("/get")
    Message get(HttpServletRequest request, @RequestParam String id) {
        String userID = redisService.queryUserID(request);
        return messageService.getMessage(userID, id);
    }

    @GetMapping("/public")
    PageInfo<Message> loadPublic(@RequestParam int pageNum, @RequestParam int pageSize) {
        return messageService.loadPublic(pageNum, pageSize);
    }

    @GetMapping("/personal")
    PageInfo<Message> loadPersonal(HttpServletRequest request,
                                   @RequestParam int pageNum, @RequestParam int pageSize) {
        String userID = redisService.queryUserID(request);
        return messageService.loadReceive(userID, pageNum, pageSize);
    }

    @GetMapping("/sent")
    PageInfo<Message> loadSent(HttpServletRequest request,
                               @RequestParam int pageNum, @RequestParam int pageSize) {
        String userID = redisService.queryUserID(request);
        return messageService.loadSent(userID, pageNum, pageSize);
    }
}
