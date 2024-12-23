package com.longfish;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.longfish.po.CourseList;
import com.longfish.po.LoginDTO;
import com.longfish.po.Result;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Core {

    private static final OkHttpClient client = new OkHttpClient().newBuilder().build();

    private static final String UserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36 Edg/126.0.0.0";

    @SuppressWarnings("all")
    public static void getCookie(String username, String password) {
        Map<String, String> cookies;

        RequestBody body1 = new FormBody.Builder()
                .add("yhm", username)
                .add("mm", password)
                .build();
        Request request1 = new Request.Builder()
                .url("https://starxin.link/register/register/")
                .method("POST", body1)
                .addHeader("User-Agent", UserAgent)
                .build();
        LoginDTO entity;
        while (true) {
            try {
                Response response = client.newCall(request1).execute();
                assert response.body() != null;
                entity = JSONObject.toJavaObject((JSON) JSON.parse(response.body().string()), LoginDTO.class);
                break;
            } catch (IOException e) {
                System.err.println("请求超时！");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }
        cookies = entity.getKey_cookies();

        if (cookies.get("JSESSIONID") == null) {
            System.err.println("账号或密码错误！");
            return;
        }
        GlobalVariableList.variables.put("cookie",
                "ADCCookie-47873-sg_210.38.137.108=" + cookies.get("ADCCookie-47873-sg_210.38.137.108")
                        + ";JSESSIONID=" + cookies.get("JSESSIONID"));
    }

    public static void getList() {
        String cookie = (String) GlobalVariableList.variables.get("cookie");
        if (cookie == null) {
            System.err.println("cookie 为空");
            return;
        }
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://jw.gdou.edu.cn/xsxk/zzxkyzb_cxZzxkYzbPartDisplay.html?" +
                        "xkxnm=2024&" +
                        "xkxqm=3&" +
                        "kklxdm=05&" +
                        "kspage=1&" +
                        "jspage=1000&" +
                        "jxbzb=")
                .method("POST", body)
                .addHeader("Cookie", cookie)
                .addHeader("User-Agent", UserAgent)
                .build();
        try {
            Response response = client.newCall(request).execute();
            assert response.body() != null;
            CourseList list = JSONObject.toJavaObject((JSON) JSON.parse(response.body().string()), CourseList.class);
            GlobalVariableList.variables.put("courseList", list);
            System.out.println(list.getTmpList());

        } catch (IOException e) {
            System.err.println("获取选课列表失败！");
        }
    }

    public static String filterList(String name) {
        CourseList list = (CourseList) GlobalVariableList.variables.get("courseList");
        List<Map<String, String>> tmpList = list.getTmpList();
        for (Map<String, String> mapList : tmpList) {
            if (mapList.get("kcmc").contains(name)) {
                String kchId = mapList.get("kch_id");
                if (kchId != null)
                    return kchId;
                String kch = mapList.get("kch");
                if (kch == null) {
                    System.err.println("课程不存在！");
                    return null;
                }
                return kch;
            }
        }
        return null;
    }

    public static List<String> getDetail(String kchId) {
        String cookie = (String) GlobalVariableList.variables.get("cookie");
        if (cookie == null) {
            System.err.println("cookie 为空");
            return null;
        }
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://jw.gdou.edu.cn/xsxk/zzxkyzbjk_cxJxbWithKchZzxkYzb.html?" +
                        "kch_id=" + kchId + "&" +
                        "xqh_id=1&" +
                        "njdm_id=2023&" +
                        "sfkxq=0&" +
                        "xkxnm=2024&" +
                        "xkxqm=3&" +
                        "kklxdm=05&" +
                        "xkkz_id=1CB2D0055EF22F03E063778926D222C9")
                .method("POST", body)
                .addHeader("Cookie", cookie)
                .addHeader("User-Agent", UserAgent)
                .build();
        List<String> resultList = new ArrayList<>();
        try {
            Response response = client.newCall(request).execute();
            assert response.body() != null;
            List<Map> result = JSONObject.parseArray(response.body().string(), Map.class);

            result.forEach(r -> {
                String doJxbId = (String) r.get("do_jxb_id");
                System.out.println(doJxbId);
                resultList.add(doJxbId);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    public static Result select(String jxbIds, String kchId, String kklxdm, String xkkzId) {
        String cookie = (String) GlobalVariableList.variables.get("cookie");
        if (cookie == null) {
            System.err.println("cookie 为空");
            return null;
        }
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://jw.gdou.edu.cn/xsxk/zzxkyzbjk_xkBcZyZzxkYzb.html?" +
                        "jxb_ids=" + jxbIds + "&" +
                        "kch_id=" + kchId + "&" +
                        "rwlx=2&" +
                        "rlkz=0&" +
                        "rlzlkz=1&" +
                        "sxbj=1&" +
                        "xxkbj=0" +
                        "&qz=0&" +
                        "cxbj=0&" +
                        "xkkz_id=" + xkkzId + "&" +
                        "njdm_id=2023&" +
                        "zyh_id=1701&" +
                        "kklxdm=" + kklxdm + "&" +
                        "xklc=2&" +
                        "xkxnm=2024&" +
                        "xkxqm=3&" +
                        "jcxx_id=")
                .method("POST", body)
                .addHeader("Cookie", cookie)
                .addHeader("User-Agent", UserAgent)
                .build();
        try {
            Result result = JSONObject.toJavaObject((JSON) JSON.parse(Objects.requireNonNull(client.newCall(request).execute().body()).string()), Result.class);
            System.out.println(result);
            return result;
        } catch (IOException e) {
            System.err.println("选课失败！");
        }
        return null;
    }
}
