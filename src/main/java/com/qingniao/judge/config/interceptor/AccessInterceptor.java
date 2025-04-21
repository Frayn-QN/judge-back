package com.qingniao.judge.config.interceptor;

import com.qingniao.judge.service.redis.RedisServiceImpl;
import com.qingniao.judge.config.entity.BusinessException;
import com.qingniao.judge.config.entity.ReturnCode;
import com.qingniao.judge.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
public class AccessInterceptor implements HandlerInterceptor {// 拦截器
    private RedisServiceImpl redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String token = TokenUtil.extractToken(request);
        if(!redisService.hasToken(token)) {
            throw new BusinessException("Interceptor", ReturnCode.RC_401);
        }

        redisService.refreshToken(token);
        return true;
    }
}
