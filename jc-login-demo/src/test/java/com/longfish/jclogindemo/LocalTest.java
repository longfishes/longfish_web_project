package com.longfish.jclogindemo;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalTest {

    @Test
    @SuppressWarnings("all")
    public void test1() {
        System.out.println("aaa".equals(null));
    }

    @Test
    public void testCheck() {
        String email = "test@example.com";

        String EMAIL_PATTERN = "^[A-Za-z0-9_]+([.\\-][A-Za-z0-9_]+)*@[A-Za-z0-9]+([.\\-][A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        boolean isValid = matcher.matches();

        System.out.println(isValid);
    }

    @Test
    public void testCase() {

    }
}
