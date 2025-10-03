package com.doors.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.temporaryRedirect;

@Configuration
public class WebConfig implements WebFluxConfigurer {

    @Bean
    public RouterFunction<ServerResponse> rootRedirect() {
        return route(GET("/"), req -> 
            temporaryRedirect(URI.create("/admin/routes")).build()
        );
    }
}
