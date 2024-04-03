package com.longfish.jclogindemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtTest {

    @Value("${jwt.ttl}")
    private Long ttl;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Test
    public void testJwt() {
        System.out.println(ttl);
        System.out.println(secretKey);
    }
}
