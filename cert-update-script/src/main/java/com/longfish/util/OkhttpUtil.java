package com.longfish.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.longfish.entity.Cert;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

public class OkhttpUtil {

    public static Cert getCert(){
        long timestamp = new Date().getTime();
        String apiId = "push-n2x740mke98kwyzj";
        String apiKey = "9aa7773c6fed74f64eb9e1aac2642fbe";
        String certificateId = "cert-3ejqm8p4onx8gd7n";

        String[] paramsForSign = new String[]{
                "timestamp=" + timestamp,
                "apiKey=" + apiKey,
                "apiId=" + apiId,
                "certificateId=" + certificateId
        };
        Arrays.sort(paramsForSign);

        StringBuilder sb = new StringBuilder();
        int len = paramsForSign.length;
        for (int i = 0; i < len - 1; i++) {
            sb.append(paramsForSign[i]).append('&');
        }
        sb.append(paramsForSign[len - 1]);
        String stringForSign = sb.toString();
        String sign = MD5.md5(stringForSign);

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url("https://ohttps.com/api/open/getCertificate?" + stringForSign + "&sign=" + sign)
                .method("GET", null)
                .build();
        Response response;
        JSON json;
        while (true) {
            try {
                response = client.newCall(request).execute();
                assert response.body() != null;
                json = (JSON) JSON.parse(response.body().string());
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return JSONObject.toJavaObject(json, Cert.class);
    }
}
