package com.longfish.jclogindemo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCodeEnum {

    SUCCESS(200, "操作成功"),

    FAIL(500, "操作失败"),

    PASSWORD_ERROR(40001, "密码错误"),

    NO_LOGIN(40001, "用户未登录"),

    CODE_ERROR(40001, "验证码错误"),

    USER_EXIST(40002, "用户已存在"),

    USER_NOT_EXIST(40002, "用户不存在"),

    USER_NAME_OR_PASSWORD_IS_NULL(40003, "用户名或密码不能为空"),

    AUTHORIZED(40300, "没有操作权限"),

    SYSTEM_ERROR(50000, "系统异常"),

    VALID_ERROR(52000, "参数格式不正确");

    private final Integer code;

    private final String desc;

}
