package com.zjq.chatbot.mapper;

import com.zjq.chatbot.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    UserEntity findByUsername(String username);

    void insert(UserEntity user);
}
