package com.longfish.jclogindemo.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomUtil {

    private final Random random = new Random(System.currentTimeMillis() / 114514 * 5);

    public String getRandomCode() {
        StringBuilder s = new StringBuilder(String.valueOf(random.nextInt(1000000)));
        if (s.length() < 7) {
            int len = 6 - s.length();
            for (int i = 0; i < len; i++) {
                s.insert(0, '0');
            }
        }
        return s.toString();
    }
}
