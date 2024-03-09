package com.longfish.ddns.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.longfish.ddns.entity.RecordListResp;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

@Slf4j
public class RequestUtil {

    public static RecordListResp executeQuery(String secretId, String secretKey) {
        Map response = null;
        try {
            String token = "";
            String version = "2021-03-23";
            String action = "DescribeRecordList";
            String body = "{\"Domain\":\"longfish.site\"}";
            String region = "";
            Response resp = doRequest(secretId, secretKey, version, action, body, region, token);
            assert resp.body() != null;
            JSON json = (JSON) JSON.parse(resp.body().string());
            response = JSONObject.toJavaObject(json, Map.class);
        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("网络错误！, {}", e.getMessage());
        } catch (InvalidKeyException e) {
            log.error("密钥错误！, {}", e.getMessage());
            System.exit(-1);
        }

        assert response != null;
        if (response.get("Response") == null) {
            log.error("网络错误！");
            return null;
        }
        return JSONObject.toJavaObject((JSON) response.get("Response"), RecordListResp.class);
    }

    public static Response executeDNS(String secretId, String secretKey, String recordId, String ipv6, String subdomain) {
        try {
            String token = "";
            String version = "2021-03-23";
            String action = "ModifyRecord";
            String body = "{\"Domain\":\"longfish.site\",\"RecordType\":\"AAAA\",\"RecordLine\":\"默认\",\"Value\":\"" + ipv6 + "\",\"RecordId\":" + recordId + ",\"SubDomain\":\"" + subdomain + "\"}";
            String region = "";
            return doRequest(secretId, secretKey, version, action, body, region, token);
        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("网络错误！, {}", e.getMessage());
        } catch (InvalidKeyException e) {
            log.error("密钥错误！, {}", e.getMessage());
            System.exit(-1);
        }

        return null;
    }

    private static final OkHttpClient client = new OkHttpClient();

    private static Response doRequest(
            String secretId, String secretKey,
            String version, String action,
            String body, String region, String token
    ) throws IOException, NoSuchAlgorithmException, InvalidKeyException {

        Request request = buildRequest(secretId, secretKey, version, action, body, region, token);
        return client.newCall(request).execute();
    }

    private static Request buildRequest(
            String secretId, String secretKey,
            String version, String action,
            String body, String region, String token
    ) throws NoSuchAlgorithmException, InvalidKeyException {
        String host = "dnspod.tencentcloudapi.com";
        String endpoint = "https://" + host;
        String contentType = "application/json; charset=utf-8";
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String auth = getAuth(secretId, secretKey, host, contentType, timestamp, body);
        return new Request.Builder()
                .header("Host", host)
                .header("X-TC-Timestamp", timestamp)
                .header("X-TC-Version", version)
                .header("X-TC-Action", action)
                .header("X-TC-Region", region)
                .header("X-TC-Token", token)
                .header("X-TC-RequestClient", "SDK_JAVA_BAREBONE")
                .header("Authorization", auth)
                .url(endpoint)
                .post(RequestBody.create(MediaType.parse(contentType), body))
                .build();
    }

    private static String getAuth(
            String secretId, String secretKey, String host, String contentType,
            String timestamp, String body
    ) throws NoSuchAlgorithmException, InvalidKeyException {
        String canonicalUri = "/";
        String canonicalQueryString = "";
        String canonicalHeaders = "content-type:" + contentType + "\nhost:" + host + "\n";
        String signedHeaders = "content-type;host";

        String hashedRequestPayload = sha256Hex(body.getBytes(StandardCharsets.UTF_8));
        String canonicalRequest = "POST"
                + "\n"
                + canonicalUri
                + "\n"
                + canonicalQueryString
                + "\n"
                + canonicalHeaders
                + "\n"
                + signedHeaders
                + "\n"
                + hashedRequestPayload;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String date = sdf.format(new Date(Long.parseLong(timestamp + "000")));
        String service = host.split("\\.")[0];
        String credentialScope = date + "/" + service + "/" + "tc3_request";
        String hashedCanonicalRequest =
                sha256Hex(canonicalRequest.getBytes(StandardCharsets.UTF_8));
        String stringToSign =
                "TC3-HMAC-SHA256\n" + timestamp + "\n" + credentialScope + "\n" + hashedCanonicalRequest;

        byte[] secretDate = hmac256(("TC3" + secretKey).getBytes(StandardCharsets.UTF_8), date);
        byte[] secretService = hmac256(secretDate, service);
        byte[] secretSigning = hmac256(secretService, "tc3_request");
        String signature =
                printHexBinary(hmac256(secretSigning, stringToSign)).toLowerCase();
        return "TC3-HMAC-SHA256 "
                + "Credential="
                + secretId
                + "/"
                + credentialScope
                + ", "
                + "SignedHeaders="
                + signedHeaders
                + ", "
                + "Signature="
                + signature;
    }

    public static String sha256Hex(byte[] b) throws NoSuchAlgorithmException {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-256");
        byte[] d = md.digest(b);
        return printHexBinary(d).toLowerCase();
    }

    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    public static String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

    public static byte[] hmac256(byte[] key, String msg) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, mac.getAlgorithm());
        mac.init(secretKeySpec);
        return mac.doFinal(msg.getBytes(StandardCharsets.UTF_8));
    }
}
