package com.longfish.jclogindemo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCodeEnum {

    SUCCESS(1, "操作成功"),

    FAIL(0, "操作失败"),

    PASSWORD_ERROR(40001, "密码错误"),

    NO_LOGIN(40001, "用户未登录"),

    AUTHORIZED(40300, "没有操作权限"),

    SYSTEM_ERROR(50000, "系统异常"),

    VALID_ERROR(52000, "参数格式不正确"),

    USERNAME_EXIST(52001, "用户名已存在"),

    USERNAME_NOT_EXIST(52002, "用户名不存在");

    private final Integer code;

    private final String desc;

}
