package com.longfish.jclogindemo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInvalidException extends RuntimeException{

    private final Integer code;

    private final String message;

    public SignInvalidException() {
        this.code = 400;
        this.message = "无效的签名";
    }
}
