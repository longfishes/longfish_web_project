package com.longfish.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RoomRequests {

    private static final String prefix = "http://k.longfish.site:7799";

    // 创建房间请求
    public static String roomCreate(String roomNumber) {
        String url = prefix + "/room/create?roomNumber=" + roomNumber;
        try {
            return sendGetRequest(url);
        } catch (Exception ignore) {}
        return "err";
    }

    // 加入房间请求
    public static String roomJoin(String roomNumber) {
        String url = prefix + "/room/join?roomNumber=" + roomNumber;
        try {
            return sendGetRequest(url);
        } catch (Exception ignore) {}
        return "err";
    }

    // 通用的 GET 请求方法
    private static String sendGetRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);  // 设置超时时间
        connection.setReadTimeout(5000);     // 设置读取超时时间

        // 获取响应码
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            return "err";  // 如果返回不是 200，返回错误
        }

        // 读取响应数据
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public static void post(int port) {
        try {
            String urlString = prefix + "/chess/create?port=" + port;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("请求成功，房间已创建！");
            } else {
                System.out.println("请求失败，响应码：" + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
