package com.doors.gateway.controller;

import com.doors.gateway.model.GatewayRouteDefinition;
import com.doors.gateway.service.DynamicRouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class GatewayRestController {

    private final DynamicRouteService dynamicRouteService;

    @GetMapping
    public Flux<GatewayRouteDefinition> getAllRoutes() {
        return dynamicRouteService.getAllRoutes();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<GatewayRouteDefinition>> getRouteById(@PathVariable String id) {
        return dynamicRouteService.getRouteById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<String>> createRoute(@Valid @RequestBody GatewayRouteDefinition route) {
        return dynamicRouteService.addRoute(route)
                .map(result -> ResponseEntity.status(HttpStatus.CREATED).body(result))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<String>> updateRoute(@PathVariable String id,
                                                     @Valid @RequestBody GatewayRouteDefinition route) {
        route.setId(id);
        return dynamicRouteService.updateRoute(route)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<String>> deleteRoute(@PathVariable String id) {
        return dynamicRouteService.deleteRoute(id)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }
}
