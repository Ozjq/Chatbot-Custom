package com.zjq.chatbot.jwt;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.zjq.chatbot.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    /**
     * 生成 Token
     */
    public String generateToken(Long userId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        
        // 设置过期时间 (JWT 标准 exp 是秒)
        long now = System.currentTimeMillis();
        // 假设配置是毫秒，转成秒
        long expireTimeInSeconds = (now + jwtProperties.getExpire()) / 1000; 
        payload.put(JWT.EXPIRES_AT, expireTimeInSeconds);

        // 使用 HS256 签名
        JWTSigner signer = JWTSignerUtil.hs256(jwtProperties.getSecret().getBytes());
        
        return JWTUtil.createToken(payload, signer);
    }

    /**
     * 解析 Token 获取 userId
     */
    public Long parseUserId(String token) {
        JWT jwt = JWTUtil.parseToken(token);

        // 如果开启了校验，则进行签名和过期检查
        if (jwtProperties.isEnabled()) {
            JWTSigner signer = JWTSignerUtil.hs256(jwtProperties.getSecret().getBytes());
            
            // 1. 验证签名
            if (!jwt.verify(signer)) {
                throw new RuntimeException("Token 签名无效");
            }

            // 2. 验证是否过期
            if (!jwt.validate(0)) { // 0 表示容忍时间差
                throw new RuntimeException("Token 已过期");
            }
        }

        // 3. 提取 userId
        Object userIdObj = jwt.getPayload("userId");
        if (userIdObj == null) {
            throw new RuntimeException("Token 中缺少 userId");
        }

        return Long.valueOf(userIdObj.toString());
    }
}
