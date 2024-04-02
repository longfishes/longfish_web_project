package com.longfish.jclogindemo.handler;

import com.longfish.jclogindemo.enums.StatusCodeEnum;
import com.longfish.jclogindemo.exception.BizException;
import com.longfish.jclogindemo.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    public Result<String> exceptionHandler(Exception ex) {
        log.debug("异常信息：{}", ex.getMessage());
        return Result.error(StatusCodeEnum.FAIL);
    }

    @ExceptionHandler
    public Result<String> exceptionHandler(BizException ex) {
        log.debug("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }
}
