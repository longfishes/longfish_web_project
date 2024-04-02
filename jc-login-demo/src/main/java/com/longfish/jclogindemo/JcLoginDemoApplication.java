package com.longfish.jclogindemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.longfish.jclogindemo.mapper")
public class JcLoginDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(JcLoginDemoApplication.class, args);
    }
}
