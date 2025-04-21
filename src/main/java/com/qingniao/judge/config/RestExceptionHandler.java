package com.qingniao.judge.config;


import com.qingniao.judge.config.entity.BusinessException;
import com.qingniao.judge.config.entity.Result;
import com.qingniao.judge.config.entity.ReturnCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
@ResponseBody
public class RestExceptionHandler {
    // 定义的业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<String> businessException(BusinessException e) {
        log.error("BusinessException: service={} code={}, message={}",
                e.getService(), e.getCode(), e.getMessage(), e);
        return Result.failure(e.getCode(), e.getMessage());
    }

    // 空指针异常前端参数错误
    @ExceptionHandler(NullPointerException.class)
    public Result<String> nullPointerException(NullPointerException e) {
        log.error("NullPointerException: ", e);
        return Result.failure(ReturnCode.RC_400.getCode(), "NullPrt Error");
    }

    // 调用不存在的接口 404
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<String> noHandlerFoundException(NoHandlerFoundException e) {
        log.error("404 NoHandlerFoundException: ", e);
        return Result.failure(ReturnCode.RC_404.getCode(), ReturnCode.RC_404.getMsg());
    }

    // 其他异常
    @ExceptionHandler(Exception.class)
    public Result<String> exception(Exception e) {
        log.error("Exception: message={}", e.getMessage(), e);
        return Result.failure(ReturnCode.RC_500.getCode(), ReturnCode.RC_500.getMsg());
    }
}
