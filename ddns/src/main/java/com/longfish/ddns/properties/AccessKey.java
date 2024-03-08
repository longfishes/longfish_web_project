package com.longfish.ddns.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "tencent")
@Data
public class AccessKey {

    private String secretId;

    private String secretKey;
}
