package com.longfish.jclogindemo.handler;

import com.longfish.jclogindemo.enums.StatusCodeEnum;
import com.longfish.jclogindemo.exception.BizException;
import com.longfish.jclogindemo.pojo.Result;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    public Result bizExceptionHandler(BizException ex, HttpServletResponse resp) {
        log.info("异常信息：{}", ex.getMessage());
        if (ex.getStatusCodeEnum() != null) {
            resp.setStatus(ex.getStatusCodeEnum().getCode());
            return Result.error(ex.getStatusCodeEnum());
        }
        resp.setStatus(StatusCodeEnum.FAIL.getCode());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    public Result noHandlerFoundExceptionHandler(NoHandlerFoundException ex, HttpServletResponse resp) {
        log.info("不存在的路径, {}", ex.getMessage());
        resp.setStatus(StatusCodeEnum.NOT_FOUND.getCode());
        return Result.error(StatusCodeEnum.NOT_FOUND);
    }

    @ExceptionHandler
    public Result exceptionHandler(Exception ex, HttpServletResponse resp) {
        log.error("异常信息：{}", ex.getMessage());
        ex.printStackTrace();
        resp.setStatus(StatusCodeEnum.FAIL.getCode());
        return Result.error(StatusCodeEnum.FAIL);
    }
}
