package com.qingniao.judge.config;

import com.qingniao.judge.config.interceptor.AccessInterceptor;
import com.qingniao.judge.config.interceptor.CorsInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private AccessInterceptor accessInterceptor;
    private CorsInterceptor corsInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {// 配置拦截器
        registry.addInterceptor(corsInterceptor)
                        .addPathPatterns("/**");
        registry.addInterceptor(accessInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/account/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 对所有路径应用此规则
                .allowedOrigins("http://localhost:9528")
                .allowedMethods("*")
                .allowedHeaders("Authorization", "Content-Type", "*")
                .allowCredentials(true); // 允许发送Cookie
    }
}
