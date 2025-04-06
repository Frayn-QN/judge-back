package com.qingniao.judge.config;

import com.qingniao.judge.config.entity.BusinessException;
import com.qingniao.judge.config.entity.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
@AllArgsConstructor
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof String) {
            try {
                // 如果Controller返回String的话，SpringBoot不会自动封装Result而直接返回
                // 因此需要手动转换成json
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(Result.success(body));
            }
            catch (JsonProcessingException e) {
                throw new BusinessException(null, e);
            }
        }
        // 已经封装好
        if (body instanceof Result<?>) {
            response.setStatusCode(HttpStatusCode.valueOf(((Result<?>) body).getCode()));
            return body;
        }

        return Result.success(body);
    }
}
