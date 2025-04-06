package com.qingniao.judge.service.business;

import com.github.pagehelper.PageInfo;
import com.qingniao.judge.entity.Message;

public interface MessageService {
    void addMessage(String userID, Message message);
    void deleteMessage(String userID, String messageID);
    Message getMessage(String userID, String messageID);
    PageInfo<Message> loadSent(String userID, int pageNum, int pageSize);
    PageInfo<Message> loadReceive(String userID, int pageNum, int pageSize);
    PageInfo<Message> loadPublic(int pageNum, int pageSize);
}
