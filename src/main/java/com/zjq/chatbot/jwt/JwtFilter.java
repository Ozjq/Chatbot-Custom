package com.zjq.chatbot.jwt;

import com.zjq.chatbot.config.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor  //为类中所有 final 修饰的字段（以及标记为 @NonNull 的字段）生成一个构造函数。
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil; // 必须加 final，否则 @RequiredArgsConstructor 不生效

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String header = request.getHeader("Authorization");
            
            if (header != null && header.startsWith("Bearer ")) {
                // 截取 Token 并去除首尾空格，防止多余空格导致解析失败
                String token = header.substring(7).trim();
                
                try {
                    Long userId = jwtUtil.parseUserId(token);
                    if (userId != null) {
                        UserContext.setUserId(userId);
                    }
                } catch (Exception e) {
                    // Token 解析失败（过期或非法）
                    logger.error("Token invalid: " + e.getMessage());
                }
            }

            filterChain.doFilter(request, response);

        } finally {
            UserContext.clear();
        }
    }
}
