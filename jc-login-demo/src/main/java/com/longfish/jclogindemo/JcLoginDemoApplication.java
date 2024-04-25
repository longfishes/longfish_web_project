package com.longfish.jclogindemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.longfish.jclogindemo.mapper")
@EnableScheduling
public class JcLoginDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(JcLoginDemoApplication.class, args);
    }
}
