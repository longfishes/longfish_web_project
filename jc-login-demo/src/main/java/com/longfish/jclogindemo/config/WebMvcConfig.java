package com.longfish.jclogindemo.config;

import com.longfish.jclogindemo.interceptor.JwtTokenUserInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
@Slf4j
public class WebMvcConfig extends WebMvcConfigurationSupport {

    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("addInterceptors...");

        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/userAuth/**")
                .excludePathPatterns("/common/code")
                .excludePathPatterns("/ws/**")
                .excludePathPatterns("/")
                .excludePathPatterns("/favicon.ico");
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
