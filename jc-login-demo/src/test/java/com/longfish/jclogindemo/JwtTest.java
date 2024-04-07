package com.longfish.jclogindemo;

import com.longfish.jclogindemo.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static com.longfish.jclogindemo.constant.CommonConstant.USER_ID;

@SpringBootTest
public class JwtTest {

    @Value("${jwt.ttl}")
    private Long ttl;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Test
    public void testCreate() {
        Map<String, Object> claims = new HashMap<>();
        claims.put(USER_ID, 2);

        String jwt = JwtUtil.createJWT(secretKey, ttl, claims);
        System.out.println(jwt);
        Claims map = JwtUtil.parseJWT(secretKey, jwt);
        System.out.println(map);
    }

    @Test
    public void testJwt() {
        System.out.println(ttl);
        System.out.println(secretKey);
    }
}
