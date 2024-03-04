package com.longfish;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;

public class Main {

    private static String cookie;

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        CJ cj;
        do {
            System.out.print("学号: ");
            String account = scanner.nextLine();
            System.out.print("密码: ");
            String password = scanner.nextLine();
            cj = gain(account, password);
        } while (cj == null);
        Arrays.stream(cj.getItems()).forEach(CJ::showSimply);

        while (true) {
            System.out.print("退出？(Y/N): ");
            String line = scanner.nextLine();

            if (line.toUpperCase(Locale.ROOT).equals("Y")) break;
        }
    }

    public static CJ gain(String account, String password) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Map<String, String> cookies;

        System.out.println("登录中...");
        RequestBody body1 = new FormBody.Builder()
                .add("yhm", account)
                .add("mm", password)
                .build();
        Request request1 = new Request.Builder()
                .url("https://starxin.link/register/register/")
                .method("POST", body1)
                .addHeader("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                .build();
        LoginDTO entity;
        while (true) {
            try {
                Response response = client.newCall(request1).execute();
                assert response.body() != null;
                entity = JSONObject.toJavaObject((JSON) JSON.parse(response.body().string()), LoginDTO.class);
                break;
            } catch (IOException e) {
                System.out.println("请求超时！");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }
        cookies = entity.getKey_cookies();

        if (cookies.get("JSESSIONID") == null) {
            System.out.println("账号或密码错误！");
            return null;
        }

        cookie = "ADCCookie-47873-sg_210.38.137.108=" + cookies.get("ADCCookie-47873-sg_210.38.137.108") + ";JSESSIONID=" + cookies.get("JSESSIONID");

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");


        Request request = new Request.Builder()
                .url("https://jw.gdou.edu.cn/cjcx/cjcx_cxXsgrcj.html?doType=query&gnmkdm=N305005&xnm=&xqm=&queryModel.showCount=1000")
                .method("POST", body)
                .addHeader("Cookie", cookie)
                .addHeader("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                .build();
        JSON json;
        while (true) {
            try {
                Response response = client.newCall(request).execute();
                assert response.body() != null;
                json = (JSON) JSON.parse(response.body().string());
                break;
            } catch (IOException e) {
                System.out.println("请求超时！");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }
        CJ cj = JSONObject.toJavaObject(json, CJ.class);
        Arrays.stream(cj.getItems()).forEach(item -> {
            Request request2 = new Request.Builder()
                    .url("https://jw.gdou.edu.cn/cjcx/cjcx_cxCjxqGjh.html?gnmkdm=N305005&jxb_id=" + item.get("jxb_id") + "&xnm=" + item.get("xnm") + "&xqm=" + item.get("xqm"))
                    .method("POST", body)
                    .addHeader("Cookie", cookie)
                    .addHeader("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                    .build();
            String html;
            while (true) {
                try {
                    Response response2 = client.newCall(request2).execute();
                    assert response2.body() != null;
                    html = response2.body().string();
                    break;
                } catch (IOException e) {
                    System.out.println("请求超时！");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }
            Document parse = Jsoup.parse(html);
            List<Detail> details = Detail.parse(parse.getElementsByTag("tbody").text());
            item.put("detail", details);
        });
        return cj;
    }
}
