package com.longfish.jclogindemo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI springShopOpenApi() {
        Contact contact = new Contact()
                .name("longfish")
                .email("longfishes@qq.com");
        Info info = new Info().title("测试接口文档")
                .description("测试")
                .version("1.0.1")
                .contact(contact);
        return new OpenAPI().info(info);
    }
}
