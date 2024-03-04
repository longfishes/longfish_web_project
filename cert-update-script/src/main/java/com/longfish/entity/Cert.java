package com.longfish.entity;

import lombok.Data;

import java.util.Map;

@Data
public class Cert {

    private Boolean success;

    private String msg;

    private Map<String, String> payload;
}
