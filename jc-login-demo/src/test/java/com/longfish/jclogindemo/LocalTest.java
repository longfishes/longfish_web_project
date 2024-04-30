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
    public void testMdToTxt() {
        String input = """
                国内的网民经常苦于无法访问境外网站如Youtube、Google、Twitter等。VPN(Virtual Private Network)属于典型的**正向代理**。

                ![vpn](https://static.longfish.site/passage/img/vpn.jpg)

                **为什么无法访问？**""";

        input = input.replaceAll("<[^>]*>", "");
        input = input.replaceAll("\n", "");
        input = input.replaceAll("#", "");
        input = input.replaceAll("`", "");
        input = input.replaceAll("~", "");
        input = input.replaceAll("\\*", "");
        input = input.replaceAll("-", "");
        input = input.replaceAll("\\s+", " ");
        input = input.replaceAll("!\\[(.*?)]\\([^)]*\\)", "$1");
        input = input.replaceAll("\\[(.*?)]\\([^)]*\\)", "$1");
        input = input.replaceAll("\\[", "");
        input = input.replaceAll(">", "");

        System.out.println(input);
    }
}
