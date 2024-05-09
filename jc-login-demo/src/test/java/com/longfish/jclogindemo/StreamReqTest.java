package com.longfish.jclogindemo;

import com.longfish.jclogindemo.ai.Util;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class StreamReqTest {

    @Test
    public void test5() {
        String baseUrl = "fjdklsajfkl&&";
        while (baseUrl.charAt(baseUrl.length() - 1) == '&')
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        System.out.println(baseUrl);
    }

    @Test
    public void test4() {
        System.out.println(convertToUnicode("hello你好"));
        System.out.println(convertToUnicode("中文"));
    }

    @Test
    public void test3() throws IOException{
        String content = convertToUnicode("hello");
        URL url = new URL("http://localhost:5000/chat?content=" + content);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "text/event-stream");

        InputStream is = connection.getInputStream();
        String s = Util.printer(is);
        System.out.println();
        System.out.println(s);
    }

    @Test
    public void test1() throws IOException {
        String content = convertToUnicode("我是你爹");
        URL url = new URL("http://localhost:5000/chat?content=" + content);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "text/event-stream");

        InputStream is = connection.getInputStream();

        try {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1) {
                System.out.write(buffer, 0, bytesRead);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String convertToUnicode(String input) {
        StringBuilder unicode = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isLetter(c) && Character.UnicodeBlock.of(c) == Character.UnicodeBlock.BASIC_LATIN) {
                unicode.append(c);
            } else {
                unicode.append("\\u").append(Integer.toHexString(c & 0xFFFF));
            }
        }
        return unicode.toString();
    }

    @Test
    public void test2() {
        System.out.println(convertToUnicode("你好"));
    }
}
