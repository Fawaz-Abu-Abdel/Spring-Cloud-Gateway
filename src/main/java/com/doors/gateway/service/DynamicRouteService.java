package com.doors.gateway.service;

import com.doors.gateway.model.GatewayFilterDefinition;
import com.doors.gateway.model.GatewayPredicateDefinition;
import com.doors.gateway.model.GatewayRouteDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicRouteService {

    private final RouteDefinitionWriter routeDefinitionWriter;
    private final RouteDefinitionLocator routeDefinitionLocator;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Add a new route dynamically
     */
    public Mono<String> addRoute(GatewayRouteDefinition gatewayRoute) {
        try {
            RouteDefinition routeDefinition = convertToRouteDefinition(gatewayRoute);
            return routeDefinitionWriter.save(Mono.just(routeDefinition))
                    .then(Mono.defer(() -> {
                        eventPublisher.publishEvent(new RefreshRoutesEvent(this));
                        log.info("Route added successfully: {}", gatewayRoute.getId());
                        return Mono.just("Route added successfully");
                    }));
        } catch (Exception e) {
            log.error("Error adding route: {}", gatewayRoute.getId(), e);
            return Mono.error(new RuntimeException("Failed to add route: " + e.getMessage()));
        }
    }

    /**
     * Update an existing route
     */
    public Mono<String> updateRoute(GatewayRouteDefinition gatewayRoute) {
        return deleteRoute(gatewayRoute.getId())
                .then(addRoute(gatewayRoute));
    }

    /**
     * Delete a route by ID
     */
    public Mono<String> deleteRoute(String routeId) {
        return routeDefinitionWriter.delete(Mono.just(routeId))
                .then(Mono.defer(() -> {
                    eventPublisher.publishEvent(new RefreshRoutesEvent(this));
                    log.info("Route deleted successfully: {}", routeId);
                    return Mono.just("Route deleted successfully");
                }))
                .onErrorResume(e -> {
                    log.error("Error deleting route: {}", routeId, e);
                    return Mono.error(new RuntimeException("Failed to delete route: " + e.getMessage()));
                });
    }

    /**
     * Get all routes
     */
    public Flux<GatewayRouteDefinition> getAllRoutes() {
        return routeDefinitionLocator.getRouteDefinitions()
                .map(this::convertToGatewayRoute);
    }

    /**
     * Get a specific route by ID
     */
    public Mono<GatewayRouteDefinition> getRouteById(String routeId) {
        return routeDefinitionLocator.getRouteDefinitions()
                .filter(route -> route.getId().equals(routeId))
                .map(this::convertToGatewayRoute)
                .next();
    }

    /**
     * Convert GatewayRouteDefinition to Spring Cloud Gateway RouteDefinition
     */
    private RouteDefinition convertToRouteDefinition(GatewayRouteDefinition gatewayRoute) {
        RouteDefinition routeDefinition = new RouteDefinition();
        routeDefinition.setId(gatewayRoute.getId());
        routeDefinition.setUri(URI.create(gatewayRoute.getUri()));

        // Convert predicates
        List<PredicateDefinition> predicates = new ArrayList<>();
        if (gatewayRoute.getPredicates() != null) {
            for (GatewayPredicateDefinition pred : gatewayRoute.getPredicates()) {
                PredicateDefinition predicateDefinition = new PredicateDefinition();
                predicateDefinition.setName(pred.getName());
                predicateDefinition.setArgs(pred.getArgs());
                predicates.add(predicateDefinition);
            }
        }
        routeDefinition.setPredicates(predicates);

        // Convert filters
        List<FilterDefinition> filters = new ArrayList<>();
        if (gatewayRoute.getFilters() != null) {
            for (GatewayFilterDefinition filter : gatewayRoute.getFilters()) {
                FilterDefinition filterDefinition = new FilterDefinition();
                filterDefinition.setName(filter.getName());
                filterDefinition.setArgs(filter.getArgs());
                filters.add(filterDefinition);
            }
        }
        routeDefinition.setFilters(filters);

        if (gatewayRoute.getOrder() != null) {
            routeDefinition.setOrder(gatewayRoute.getOrder());
        }

        return routeDefinition;
    }

    /**
     * Convert Spring Cloud Gateway RouteDefinition to GatewayRouteDefinition
     */
    private GatewayRouteDefinition convertToGatewayRoute(RouteDefinition routeDefinition) {
        GatewayRouteDefinition gatewayRoute = new GatewayRouteDefinition();
        gatewayRoute.setId(routeDefinition.getId());
        gatewayRoute.setUri(routeDefinition.getUri().toString());
        gatewayRoute.setOrder(routeDefinition.getOrder());

        // Convert predicates
        List<GatewayPredicateDefinition> predicates = new ArrayList<>();
        if (routeDefinition.getPredicates() != null) {
            for (PredicateDefinition pred : routeDefinition.getPredicates()) {
                GatewayPredicateDefinition gatewayPredicate = new GatewayPredicateDefinition();
                gatewayPredicate.setName(pred.getName());
                gatewayPredicate.setArgs(pred.getArgs());
                predicates.add(gatewayPredicate);
            }
        }
        gatewayRoute.setPredicates(predicates);

        // Convert filters
        List<GatewayFilterDefinition> filters = new ArrayList<>();
        if (routeDefinition.getFilters() != null) {
            for (FilterDefinition filter : routeDefinition.getFilters()) {
                GatewayFilterDefinition gatewayFilter = new GatewayFilterDefinition();
                gatewayFilter.setName(filter.getName());
                gatewayFilter.setArgs(filter.getArgs());
                filters.add(gatewayFilter);
            }
        }
        gatewayRoute.setFilters(filters);

        return gatewayRoute;
    }
}
