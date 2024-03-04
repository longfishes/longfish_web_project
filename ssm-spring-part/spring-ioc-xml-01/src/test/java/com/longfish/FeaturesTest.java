package com.longfish;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class FeaturesTest {
    @Test
    public void test1() {
        var a = Arrays.asList(1,2,3);
        var b = "fldsajhk";

        System.out.println(b + a.get(0));
    }
}
