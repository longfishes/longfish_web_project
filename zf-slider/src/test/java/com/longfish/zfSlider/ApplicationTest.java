package com.longfish.zfSlider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.longfish.zfSlider.resp.CodeRefreshResp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ApplicationTest {

    private static final String path = "E:\\Administrator\\桌面\\zfSlider";

    private static final OkHttpClient client = new OkHttpClient().newBuilder().build();

    private static final long timestamp = System.currentTimeMillis();

    @Test
    public void test1() throws IOException {
        Request refreshReq = new Request.Builder()
                .url("https://jw.gdou.edu.cn/zfcaptchaLogin?" +
                        "type=refresh&" +
                        "time=" + timestamp + "&" +
                        "instanceId=zfcaptchaLogin")
                .build();
        Response refreshResp = client.newCall(refreshReq).execute();
        assert refreshResp.body() != null;
        CodeRefreshResp codeRefreshResp = JSONObject.toJavaObject((JSON) JSON.parse(refreshResp.body().string()), CodeRefreshResp.class);
        List<String> headers = refreshResp.headers("Set-Cookie");

        headers.forEach(System.out::println);

        String jsi = headers.get(0).substring(11, 43);
        String adc = headers.get(1).substring(34, 46);

        System.out.println(jsi);
        System.out.println(adc);


        Request resourceReq = new Request.Builder()
                .url("https://jw.gdou.edu.cn/zfcaptchaLogin?" +
                        "type=image&" +
                        "id="+codeRefreshResp.getSi()+"&" +
                        "imtk=" + codeRefreshResp.getImtk() + "&" +
                        "t=" + codeRefreshResp.getT() + "&" +
                        "instanceId=zfcaptchaLogin")
                .header("Cookie", "JSESSIONID=" + jsi + "; ADCCookie-47873-sg_210.38.137.108=" + adc)
                .build();

        Response resourceResp = client.newCall(resourceReq).execute();
        System.out.println(resourceResp.code());

        assert resourceResp.body() != null;
        InputStream is = resourceResp.body().byteStream();

        FileOutputStream fos = new FileOutputStream(path + "\\" + System.currentTimeMillis() + ".png");

        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            fos.write(buffer, 0, length);
        }

        fos.close();
        is.close();

        System.out.println("@@");
        resourceResp.headers("Set-Cookie").forEach(System.out::println);
    }

    @Test
    public void testIter() throws IOException {
        for (int i = 0; i < 20; i++) {
            test1();
        }
    }
}
