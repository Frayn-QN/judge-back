package com.qingniao.judge.mapper;

import com.qingniao.judge.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    void insert(Message message);
    void delete(String id);
    Message selectOne(String id);
    List<Message> selectPublic();
    List<Message> selectByRecvUser(String receiveID);
    List<Message> selectBySendUser(String sendID);
}
