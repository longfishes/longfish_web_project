package com.longfish.ddns;

import com.longfish.ddns.properties.AccessKey;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.cvm.v20170312.CvmClient;
import com.tencentcloudapi.cvm.v20170312.models.DescribeInstancesRequest;
import com.tencentcloudapi.cvm.v20170312.models.DescribeInstancesResponse;
import com.tencentcloudapi.dnspod.v20210323.DnspodClient;
import com.tencentcloudapi.dnspod.v20210323.models.DescribeRecordListRequest;
import com.tencentcloudapi.dnspod.v20210323.models.DescribeRecordListResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@SpringBootTest
public class SDKTest {

    @Autowired
    private AccessKey accessKey;

    @Test
    public void test1() {
        try {
            Credential cred = new Credential(accessKey.getSecretId(), accessKey.getSecretKey());
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("dnspod.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            DnspodClient client = new DnspodClient(cred, "", clientProfile);
            DescribeRecordListRequest req = new DescribeRecordListRequest();
            req.setDomain("longfish.site");
            DescribeRecordListResponse resp = client.DescribeRecordList(req);

            System.out.println(resp);

            HashMap<String, String> map = new HashMap<>();
            resp.toMap(map, "");
            System.out.println(map);

        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2() {
        try {
            Credential cred = new Credential(accessKey.getSecretId(), accessKey.getSecretKey());
            CvmClient client = new CvmClient(cred, "ap-shanghai");

            DescribeInstancesRequest req = new DescribeInstancesRequest();
            DescribeInstancesResponse resp = client.DescribeInstances(req);

            System.out.println(DescribeInstancesResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }
    }
}
