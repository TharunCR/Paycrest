package com.Bank.Paycrest.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
public class MyConfig {
    @Bean
    public HandlerMappingIntrospector mvcIntrospector() {
        return new HandlerMappingIntrospector();
    }
}
