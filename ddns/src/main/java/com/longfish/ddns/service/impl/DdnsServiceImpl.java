package com.longfish.ddns.service.impl;

import com.longfish.ddns.entity.RecordListResp;
import com.longfish.ddns.properties.AccessKey;
import com.longfish.ddns.properties.CustomerConfig;
import com.longfish.ddns.service.DdnsService;
import com.longfish.ddns.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DdnsServiceImpl implements DdnsService {

    @Autowired
    private AccessKey accessKey;
    @Autowired
    private CustomerConfig customerConfig;

    private Long recordId;

    @Override
    public void update(String ip) {
        RecordListResp recordListResp = RequestUtil.executeQuery(accessKey.getSecretId(), accessKey.getSecretKey());
        Response resp;

        recordListResp.getRecordList().forEach(record -> {
            if (record.getName().equals(customerConfig.getSubDomain()) && record.getStatus().equals("ENABLE")) {
                if (!record.getValue().equals(ip)) {
                    recordId = record.getRecordId();
                }
                else {
                    log.info("当前ip与解析记录相同！无需添加dns记录！");
                    throw new RuntimeException("当前ip与解析记录相同！无需添加dns记录！");
                }
            }
        });
        resp = RequestUtil.executeDNS(accessKey.getSecretId(), accessKey.getSecretKey(), String.valueOf(recordId), ip, customerConfig.getSubDomain());
        assert resp != null;
        if (resp.code() == 200) {
            log.info("{}", resp);
            log.info("dns记录添加成功！");
        }
    }
}
