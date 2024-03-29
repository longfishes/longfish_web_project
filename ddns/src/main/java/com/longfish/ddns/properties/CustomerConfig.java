package com.longfish.ddns.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "customer.config")
@Data
public class CustomerConfig {

    private String subDomain;

    private String recordId;

    private String domain;
}
