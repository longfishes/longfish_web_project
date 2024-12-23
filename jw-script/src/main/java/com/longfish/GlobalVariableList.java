package com.longfish;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class GlobalVariableList {

    public static final Map<String, Object> variables = new HashMap<>();

}
