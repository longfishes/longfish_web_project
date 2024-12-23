package com.longfish.jclogindemo.ai;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Util {

    public static String printer(InputStream is) {
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
                System.out.write(buffer, 0, bytesRead);
            }
            return baos.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static InputStream req(List<String> user, List<String> assist) {
        try {
            String baseUrl = "http://k.longfish.site:9876/chat?";
            baseUrl += build("user", user);
            baseUrl += build("assistant", assist);
            while (baseUrl.charAt(baseUrl.length() - 1) == '&')
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            URL url = new URL(baseUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "text/event-stream");
            return connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String build(String paramName, List<String> list) {
        StringBuilder result = new StringBuilder();
        for (String value : list) {
            result.append(paramName).append("=").append(URLEncoder.encode(value, StandardCharsets.UTF_8)).append("&");
        }
        return result.toString();
    }

}
