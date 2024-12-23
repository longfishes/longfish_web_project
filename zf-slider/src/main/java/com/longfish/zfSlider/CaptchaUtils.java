package com.longfish.zfSlider;

import com.alibaba.fastjson.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CaptchaUtils {

    /**
     * 将浏览器信息转化为 Base64 编码的字符串。
     *
     * @param userAgent 用户代理
     * @param appName 应用名称
     * @param appVersion 应用版本
     * @return Base64 编码后的浏览器信息字符串
     */
    public static String encodeBrowserInfo(String appName, String userAgent, String appVersion) {
        // 创建一个包含浏览器信息的 JSON 对象
        JSONObject browserInfo = new JSONObject();
        browserInfo.put("appName", appName);
        browserInfo.put("userAgent", userAgent);
        browserInfo.put("appVersion", appVersion);

        // 将浏览器信息转化为 JSON 字符串
        String jsonString = browserInfo.toString();

        // 对 JSON 字符串进行 Base64 编码
        return encodeBase64(jsonString);
    }

    /**
     * 对给定的字符串进行 Base64 编码。
     *
     * @param input 需要编码的字符串
     * @return Base64 编码后的字符串
     */
    public static String encodeBase64(String input) {
        // 使用 UTF-8 编码将字符串转换为字节数组
        byte[] encodedBytes = Base64.getEncoder().encode(input.getBytes(StandardCharsets.UTF_8));
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        // 示例数据
        String appName = "Mozilla";
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
        String appVersion = "91.0.4472.124";

        // 获取 Base64 编码后的浏览器信息
        String encodedBrowserInfo = encodeBrowserInfo(appName, userAgent, appVersion);

        // 输出结果
        System.out.println("Encoded Browser Info: " + encodedBrowserInfo);
    }
}
