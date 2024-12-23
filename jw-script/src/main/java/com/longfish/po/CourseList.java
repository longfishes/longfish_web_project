package com.longfish.po;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CourseList {

    private String sfxsjc;

    private List<Map<String, String>> tmpList;
}
