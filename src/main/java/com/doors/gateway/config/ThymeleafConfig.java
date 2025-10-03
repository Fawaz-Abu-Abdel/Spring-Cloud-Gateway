package com.doors.gateway.config;

import com.doors.gateway.util.MapConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThymeleafConfig {

    @Bean(name = "mapConverter")
    public MapConverter mapConverter() {
        return new MapConverter();
    }
}
