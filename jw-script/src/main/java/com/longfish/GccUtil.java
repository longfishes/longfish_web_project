package com.longfish;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.longfish.po.Result;
import okhttp3.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GccUtil {

    private static final OkHttpClient client = new OkHttpClient()
            .newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    private static final String UserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36 Edg/126.0.0.0";

    private static final String prefix = "http://jwxt.gcc.edu.cn";

//    private static final String prefix = "https://jw.gdou.edu.cn";

    public static void getCookie() {

        Scanner sc = new Scanner(System.in);
        System.out.print("请输入jsessionid：");
        String jsessionid = sc.next();
        System.out.print("请输入route：");
        String route = sc.next();

        GlobalVariableList.variables.put("cookie",
                "JSESSIONID=" + jsessionid
                        + ";route=" + route);
    }

    public static List<String> getDetail(String kchId, String kklxdm, String xkkzId) {
        String cookie = (String) GlobalVariableList.variables.get("cookie");
        if (cookie == null) {
            System.err.println("cookie 为空");
            return null;
        }
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(prefix + "/xsxk/zzxkyzbjk_cxJxbWithKchZzxkYzb.html?" +
                        "kch_id=" + kchId + "&" +
                        "xqh_id=1&" +
                        "njdm_id=2023&" +
                        "sfkxq=0&" +
                        "xkxnm=2024&" +
                        "xkxqm=3&" +
                        "kklxdm=" + kklxdm + "&" +
                        "xkkz_id=" + xkkzId)
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
                .url(prefix + "/xsxk/zzxkyzbjk_xkBcZyZzxkYzb.html?" +
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

    public static String findXkkzId(String groupId, String keywords) {
        String cookie = (String) GlobalVariableList.variables.get("cookie");
        if (cookie == null) {
            System.err.println("cookie 为空");
            return null;
        }
        Request request = new Request.Builder()
                .url(prefix + "/xsxk/zzxkyzb_cxZzxkYzbIndex.html?gnmkdm=N253512&layout=default")
                .addHeader("Cookie", cookie)
                .addHeader("User-Agent", UserAgent)
                .build();
        String payload;
        try {
            Response response = client.newCall(request).execute();
            assert response.body() != null;
            payload = response.body().string();
        } catch (IOException e) {
            System.err.println("加载失败！");
            return null;
        }
        String regex = "[A-Z0-9]{32}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(payload);

        String ret = null;

        while (matcher.find()) {
            String xkkId = matcher.group();
            System.out.println("找到xkkId: " + xkkId);
            String target = payload.substring(matcher.start() - 5, matcher.start() - 3);
            System.out.println("类别编号为：" + target);
            if (target.equals(groupId)) {
                if (Pattern.compile(keywords).matcher(payload.substring(matcher.end(), matcher.end() + 100)).find()) {
                    System.out.println("=========== find target ============\n" + xkkId);
                    ret = xkkId;
                    System.out.println("====================================");
                    break;
                }
            }
        }
        return ret;
    }

    public static void preCheckCookie() {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        System.out.println("开始提前校验登录状态是否有效...");
        String cookie = (String) GlobalVariableList.variables.get("cookie");
        if (cookie == null) {
            System.err.println("cookie 为空");
            return;
        }
        Request request = new Request.Builder()
                .url(prefix + "/xsxk/zzxkyzb_cxZzxkYzbIndex.html?gnmkdm=N253512&layout=default")
                .addHeader("Cookie", cookie)
                .addHeader("User-Agent", UserAgent)
                .build();
        String payload = null;
        try {
            Response response = client.newCall(request).execute();
            assert response.body() != null;
            payload = response.body().string();
        } catch (Exception e) {
            System.err.println("加载失败！");
            preCheckCookie();
        }
        assert payload != null;
        if (!Pattern.compile("自主选课").matcher(payload).find()) {
            System.err.println("cookie已经过期！请重新登录");
            System.exit(0);
        }
    }
}
