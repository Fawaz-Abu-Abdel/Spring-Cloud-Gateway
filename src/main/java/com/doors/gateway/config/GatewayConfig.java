package com.doors.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    /**
     * This bean ensures that the admin UI paths are not routed through the gateway
     * They will be handled by the MVC controllers instead
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // All routes configured via YAML or dynamically will be added here automatically
                .build();
    }
}
