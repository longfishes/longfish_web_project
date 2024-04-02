package com.longfish.jclogindemo.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomUtil {

    private final Random random = new Random(System.currentTimeMillis() / 114514 * 5);

    public String getRandomCode() {
        return String.valueOf(random.nextInt(1000000));
    }
}
