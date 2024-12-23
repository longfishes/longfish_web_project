package com.longfish.po;

import lombok.Data;

import java.util.Map;

@Data
public class LoginDTO {

    private Map<String, String> key_cookies;
}
