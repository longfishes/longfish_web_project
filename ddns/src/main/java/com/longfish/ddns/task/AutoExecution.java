package com.longfish.ddns.task;

import com.longfish.ddns.entity.CurIPv6;
import com.longfish.ddns.properties.CustomerConfig;
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
    @Autowired
    private CustomerConfig customerConfig;

    @PostConstruct
    public void init() {
        CurIPv6.IPv6 = IPv6Util.getIpAddress();
        log.info("开始执行任务，获取到当前ipv6为: {}", CurIPv6.IPv6);
        boolean flag = false;
        while (true) {
            try {
                if (flag) Thread.sleep(5000);
                flag = true;
                ddnsService.update(CurIPv6.IPv6);
                break;
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        }
        log.info("初始化完成");
        log.info("通过域名 " + customerConfig.getSubDomain() + "." + customerConfig.getDomain() +" 访问您本地计算机上的服务");
    }

    @Scheduled(cron = "0/5 * * * * ?")
    public void executeTask(){
        String ip = IPv6Util.getIpAddress();
        if (!ip.equals(CurIPv6.IPv6)) {
            log.info("ip变化! : {} -> {}", CurIPv6.IPv6, ip);
            CurIPv6.IPv6 = ip;
            log.info("开始修改dns记录...");
            while (true) {
                try {
                    ddnsService.update(CurIPv6.IPv6);
                    break;
                } catch (Exception e) {
                    log.warn(e.getMessage());
                }
            }
        }
    }
}
