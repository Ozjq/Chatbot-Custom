package com.zjq.chatbot.service;

import cn.hutool.crypto.digest.BCrypt;
import com.zjq.chatbot.entity.UserEntity;
import com.zjq.chatbot.jwt.JwtUtil;
import com.zjq.chatbot.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    public void register(String username, String password) {
        // 1. 检查用户名是否已存在
        UserEntity existUser = userMapper.findByUsername(username);
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 密码加密 (使用 Hutool 的 BCrypt)
        String encodedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // 3. 构建用户实体
        UserEntity newUser = UserEntity.builder()
                .username(username)
                .password(encodedPassword)
                .role("USER") // 默认角色
                .status(1)    // 默认启用
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 4. 插入数据库
        userMapper.insert(newUser);
    }

    /**
     * 用户登录
     * @return JWT Token
     */
    public String login(String username, String password) {
        // 1. 查询用户
        UserEntity user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 校验密码 (明文 vs 密文)
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 3. 生成 Token
        return jwtUtil.generateToken(user.getId());
    }
}
