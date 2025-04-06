package com.qingniao.judge.service.business.impls;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qingniao.judge.entity.Message;
import com.qingniao.judge.enums.UserAuth;
import com.qingniao.judge.mapper.MessageMapper;
import com.qingniao.judge.mapper.UserMapper;
import com.qingniao.judge.service.business.MessageService;
import com.qingniao.judge.config.entity.BusinessException;
import com.qingniao.judge.config.entity.ReturnCode;
import com.qingniao.judge.util.UUIDUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {
    private MessageMapper messageMapper;
    private UserMapper userMapper;

    private void verify(String userID, String messageID) {
        Message message = messageMapper.selectOne(messageID);
        UserAuth authority = userMapper.selectAuthority(userID);

        if(authority.equals(UserAuth.ADMIN)) return; // 管理员通过
        if(message.getReceiveID().equals("ALL")) // 公告不能操作
            throw new BusinessException("Message", ReturnCode.RC_403);
        if(!message.getSendID().equals(userID) && !message.getReceiveID().equals(userID))// 非收发双方不能操作
            throw new BusinessException("Message", ReturnCode.RC_403);
    }

    @Override
    public void addMessage(String userID, Message message) {
        message.setId(UUIDUtil.generateUUIDStr());
        message.setSendTime(new Date());
        message.setSendID(userID);

        if(message.getReceiveID() == null) {// 为空表示不为全体消息
            String username = message.getReceiveUsername();
            message.setReceiveID(userMapper.selectByUsername(username));
        }

        messageMapper.insert(message);
    }

    @Override
    public void deleteMessage(String userID, String messageID) {
        verify(userID, messageID);

        messageMapper.delete(messageID);
    }

    @Override
    public Message getMessage(String userID, String messageID) {
        Message message = messageMapper.selectOne(messageID);
        if(!message.getReceiveID().equals("ALL"))// 非公告
            verify(userID, messageID);

        return message;
    }

    @Override
    public PageInfo<Message> loadSent(String userID, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(messageMapper.selectBySendUser(userID));
    }

    @Override
    public PageInfo<Message> loadReceive(String userID, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(messageMapper.selectByRecvUser(userID));
    }

    @Override
    public PageInfo<Message> loadPublic(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(messageMapper.selectPublic());
    }
}
