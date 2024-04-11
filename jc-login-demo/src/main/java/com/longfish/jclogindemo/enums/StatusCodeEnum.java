package com.longfish.jclogindemo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCodeEnum {

    SUCCESS(200, "操作成功"),

    FAIL(0, "操作失败"),

    NOT_FOUND(404, "not found"),

    PASSWORD_ERROR(40001, "密码错误"),

    NO_LOGIN(40001, "用户未登录"),

    CODE_ERROR(40001, "验证码错误"),

    USER_EXIST(40002, "用户已存在"),

    USER_NOT_EXIST(40002, "用户不存在"),

    AUTHORIZED(40300, "没有操作权限"),

    METHOD_NOT_ALLOWED(40500, "请求方法不允许"),

    SYSTEM_ERROR(50000, "系统异常"),

    VALID_ERROR(52000, "参数格式不正确");

    private final Integer code;

    private final String desc;

}
