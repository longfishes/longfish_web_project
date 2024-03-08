package com.longfish.ddns;

import com.longfish.ddns.properties.AccessKey;
import com.longfish.ddns.properties.CustomerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PropertiesTest {

    @Autowired
    private AccessKey accessKey;
    @Autowired
    private CustomerConfig customerConfig;

    @Test
    public void test1() {
        System.out.println(accessKey.getSecretId());
        System.out.println(accessKey.getSecretKey());
    }

    @Test
    public void test2() {
        System.out.println(customerConfig.getSubDomain());
    }
}
