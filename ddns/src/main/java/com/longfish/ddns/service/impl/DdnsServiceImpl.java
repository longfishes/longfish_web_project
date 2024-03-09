package com.longfish.ddns.service.impl;

import com.longfish.ddns.entity.RecordList;
import com.longfish.ddns.entity.RecordListResp;
import com.longfish.ddns.properties.AccessKey;
import com.longfish.ddns.properties.CustomerConfig;
import com.longfish.ddns.service.DdnsService;
import com.longfish.ddns.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DdnsServiceImpl implements DdnsService {

    @Autowired
    private AccessKey accessKey;
    @Autowired
    private CustomerConfig customerConfig;

    private String recordValue;

    @Override
    public void update(String ip) {
        RecordListResp recordListResp = RequestUtil.executeQuery(accessKey.getSecretId(), accessKey.getSecretKey());
        Response resp;
        List<RecordList> recordLists = new ArrayList<>();
        if (recordListResp.getRecordList() == null) {
            log.error("密钥错误！");
            System.exit(-1);
        }
        recordListResp.getRecordList().forEach(record -> {
            if (record.getName().equals(customerConfig.getSubDomain()) && record.getStatus().equals("ENABLE")) {
                if (!String.valueOf(record.getRecordId()).equals(customerConfig.getRecordId()))
                    recordLists.add(record);
                else recordValue = record.getValue();
            }
        });
        if (recordLists.size() != 0) {
            log.error("子域名不可用！");
            System.exit(-1);
        } else {
            if (recordValue != null && recordValue.equals(ip)) {
                throw new RuntimeException("当前解析与ip地址一致，无需添加dns记录！");
            }
        }
        resp = RequestUtil.executeDNS(accessKey.getSecretId(), accessKey.getSecretKey(), customerConfig.getRecordId(), ip, customerConfig.getSubDomain());
        assert resp != null;
        try {
            if (resp.code() == 200) {
                assert resp.body() != null;
                if (!resp.body().string().contains("Error")) {
                    log.info("{}", resp);
                    log.info("dns记录添加成功！");
                } else {
                    log.error("Invaded Record ID!");
                    System.exit(-1);
                }
            }
        } catch (IOException e) {
            log.error("网络错误！, {}", e.getMessage());
        }
    }
}
