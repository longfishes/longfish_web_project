package com.longfish;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.longfish.mapper")
public class SpringbootMybatisCrudApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootMybatisCrudApplication.class, args);
    }

}
