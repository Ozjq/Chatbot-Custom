package com.zjq.chatbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {

    private String secret;

    private long expire;

    /**
     * 是否开启 JWT 校验（默认开启）
     * 开发环境可设置为 false 以跳过过期检查
     */
    private boolean enabled = false;
}
