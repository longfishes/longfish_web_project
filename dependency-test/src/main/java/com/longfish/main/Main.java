package com.longfish.main;

import com.longfish.ddns.DdnsApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(DdnsApplication.class, args);
    }
}
