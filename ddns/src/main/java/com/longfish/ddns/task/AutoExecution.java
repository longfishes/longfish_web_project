package com.longfish.ddns.task;

import com.longfish.ddns.entity.CurIPv6;
import com.longfish.ddns.service.DdnsService;
import com.longfish.ddns.util.IPv6Util;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AutoExecution {

    @Autowired
    private DdnsService ddnsService;

    @PostConstruct
    public void init() {
        CurIPv6.IPv6 = IPv6Util.getIpAddress();
        log.info("开始执行任务，获取到当前ipv6为: {}", CurIPv6.IPv6);
        try {
            ddnsService.update(CurIPv6.IPv6);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        log.info("初始化完成");
    }

    @Scheduled(cron = "0/5 * * * * ?")
    public void executeTask(){
        String ip = IPv6Util.getIpAddress();
        if (!ip.equals(CurIPv6.IPv6)) {
            log.info("ip变化! : {} -> {}", CurIPv6.IPv6, ip);
            CurIPv6.IPv6 = ip;
            log.info("开始修改dns记录...");
            ddnsService.update(ip);
        }
    }
}
