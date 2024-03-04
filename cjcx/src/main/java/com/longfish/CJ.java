package com.longfish;

import lombok.Data;

import java.util.Map;

@Data
public class CJ {

    private Integer currentPage;

    private Integer currentResult;

    private Boolean entityOrField;

    private Map<String, Object>[] items;

    private Integer limit;

    private Integer offset;

    private Integer pageNo;

    private Integer pageSize;

    private Integer showCount;

    private String sortName;

    private String sortOrder;

    private String[] sorts;

    private Integer totalCount;

    private Integer totalPage;

    private Integer totalResult;

    public static void showSimply(Map<String, Object> item){
        System.out.print("{\"kcmc\":\"" + item.get("kcmc"));
        System.out.print("\",\"jsxm\":\"" + item.get("jsxm"));
        System.out.print("\",\"cj\":\"" + item.get("cj"));
        System.out.print("\",\"detail\":\"" + item.get("detail"));
        System.out.print("\",\"xf\":\"" + item.get("xf"));
        System.out.print("\",\"jd\":\"" + item.get("jd"));
        System.out.print("\",\"xfjd\":\"" + item.get("xfjd"));
        System.out.print("\",\"khfsmc\":\"" + item.get("khfsmc"));
        System.out.print("\",\"tjsj\":\"" + item.get("tjsj") + "\"}");
        System.out.println();
    }
}
