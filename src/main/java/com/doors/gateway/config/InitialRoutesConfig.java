package com.doors.gateway.config;

import com.doors.gateway.model.GatewayFilterDefinition;
import com.doors.gateway.model.GatewayPredicateDefinition;
import com.doors.gateway.model.GatewayRouteDefinition;
import com.doors.gateway.service.DynamicRouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitialRoutesConfig {

    private final DynamicRouteService dynamicRouteService;

    @EventListener(ApplicationReadyEvent.class)
    public void createInitialRoutes() {
        log.info("Creating initial routes...");
        
        // Create the ye-service route
        createYeServiceRoute();
        
        log.info("Initial routes created successfully");
    }

    private void createYeServiceRoute() {
        try {
            // Check if route already exists
            dynamicRouteService.getRouteById("ye-service-route")
                    .flatMap(existing -> {
                        log.info("Route 'ye-service-route' already exists, skipping creation");
                        return dynamicRouteService.getRouteById("ye-service-route");
                    })
                    .switchIfEmpty(
                        dynamicRouteService.addRoute(buildYeServiceRoute())
                            .doOnSuccess(result -> log.info("Created route: ye-service-route"))
                            .doOnError(error -> log.error("Failed to create route: ye-service-route", error))
                            .flatMap(result -> dynamicRouteService.getRouteById("ye-service-route"))
                    )
                    .subscribe();
        } catch (Exception e) {
            log.error("Error creating ye-service route", e);
        }
    }

    private GatewayRouteDefinition buildYeServiceRoute() {
        // Create predicate for Path=/ye/**
        GatewayPredicateDefinition pathPredicate = new GatewayPredicateDefinition();
        pathPredicate.setName("Path");
        Map<String, String> pathArgs = new LinkedHashMap<>();
        pathArgs.put("_genkey_0", "/ye/**");
        pathPredicate.setArgs(pathArgs);

        // Create filter for RewritePath
        GatewayFilterDefinition rewriteFilter = new GatewayFilterDefinition();
        rewriteFilter.setName("RewritePath");
        Map<String, String> rewriteArgs = new LinkedHashMap<>();
        rewriteArgs.put("_genkey_0", "/ye/(?<segment>.*)");
        rewriteArgs.put("_genkey_1", "/api/${segment}");
        rewriteFilter.setArgs(rewriteArgs);

        // Build the route
        List<GatewayPredicateDefinition> predicates = new ArrayList<>();
        predicates.add(pathPredicate);

        List<GatewayFilterDefinition> filters = new ArrayList<>();
        filters.add(rewriteFilter);

        return GatewayRouteDefinition.builder()
                .id("ye-service-route")
                .uri("http://192.168.0.188:8000")
                .predicates(predicates)
                .filters(filters)
                .order(0)
                .description("Route to YE service - rewrites /ye/** to /api/**")
                .build();
    }
}
