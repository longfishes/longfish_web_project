package com.longfish.ddns.app;

import com.longfish.ddns.entity.RecordListResp;
import com.longfish.ddns.properties.AccessKey;
import com.longfish.ddns.service.DdnsService;
import com.longfish.ddns.util.RequestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ApplicationTest {

    @Autowired
    private AccessKey accessKey;
    @Autowired
    private DdnsService ddnsService;

    @Test
    public void testReq() {
        RecordListResp recordListResp = RequestUtil.executeQuery(accessKey.getSecretId(), accessKey.getSecretKey());

        System.out.println(recordListResp.getRecordList());

        recordListResp.getRecordList().forEach(record -> {
            if (record.getName().equals("test")) {
                System.out.println(record.getRecordId());
            }
        });
    }

    @Test
    public void testUpdate() {
        try {
            ddnsService.update("::1");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
