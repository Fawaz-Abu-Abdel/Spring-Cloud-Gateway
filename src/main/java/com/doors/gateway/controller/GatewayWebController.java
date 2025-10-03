package com.doors.gateway.controller;

import com.doors.gateway.model.GatewayRouteDefinition;
import com.doors.gateway.model.GatewayPredicateDefinition;
import com.doors.gateway.model.GatewayFilterDefinition;
import com.doors.gateway.model.SimpleRouteForm;
import com.doors.gateway.service.DynamicRouteService;
import com.doors.gateway.util.MapConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class GatewayWebController {

    private final DynamicRouteService dynamicRouteService;

    @GetMapping("/routes")
    public Mono<String> listRoutes(Model model) {
        return dynamicRouteService.getAllRoutes()
                .collectList()
                .doOnNext(routes -> model.addAttribute("routes", routes))
                .thenReturn("routes/index");
    }

    @GetMapping("/routes/new")
    public String showCreateForm(Model model) {
        GatewayRouteDefinition route = new GatewayRouteDefinition();
        route.setPredicates(new ArrayList<>());
        route.setFilters(new ArrayList<>());
        model.addAttribute("route", route);
        model.addAttribute("isEdit", false);
        return "routes/advance-route-form";
    }

    @GetMapping("/routes/edit/{id}")
    public Mono<String> showEditForm(@PathVariable String id, Model model) {
        return dynamicRouteService.getRouteById(id)
                .flatMap(route -> {
                    model.addAttribute("route", route);
                    model.addAttribute("isEdit", true);
                    return Mono.just("routes/advance-route-form");
                })
                .switchIfEmpty(Mono.defer(() -> {
                    model.addAttribute("error", "Route not found: " + id);
                    return dynamicRouteService.getAllRoutes()
                            .collectList()
                            .doOnNext(routes -> model.addAttribute("routes", routes))
                            .thenReturn("routes/index");
                }));
    }

    @PostMapping("/routes/save")
    public Mono<String> saveRoute(@ModelAttribute GatewayRouteDefinition route,
                                   @RequestParam(required = false) String isEdit,
                                   Model model) {
        // Clean up empty predicates and filters
        if (route.getPredicates() != null) {
            route.getPredicates().removeIf(p -> p.getName() == null || p.getName().trim().isEmpty());
        }
        if (route.getFilters() != null) {
            route.getFilters().removeIf(f -> f.getName() == null || f.getName().trim().isEmpty());
        }

        Mono<String> operation = "true".equals(isEdit) 
            ? dynamicRouteService.updateRoute(route)
            : dynamicRouteService.addRoute(route);

        return operation
                .flatMap(result -> {
                    String message = "true".equals(isEdit) 
                        ? "Route updated successfully: " + route.getId()
                        : "Route created successfully: " + route.getId();
                    model.addAttribute("success", message);
                    return dynamicRouteService.getAllRoutes()
                            .collectList()
                            .doOnNext(routes -> model.addAttribute("routes", routes))
                            .thenReturn("routes/index");
                })
                .onErrorResume(e -> {
                    log.error("Error saving route", e);
                    model.addAttribute("error", "Error saving route: " + e.getMessage());
                    model.addAttribute("route", route);
                    model.addAttribute("isEdit", "true".equals(isEdit));
                    return Mono.just("routes/advance-route-form");
                });
    }

    @PostMapping("/routes/delete/{id}")
    public Mono<String> deleteRoute(@PathVariable String id, Model model) {
        return dynamicRouteService.deleteRoute(id)
                .flatMap(result -> {
                    model.addAttribute("success", "Route deleted successfully: " + id);
                    return dynamicRouteService.getAllRoutes()
                            .collectList()
                            .doOnNext(routes -> model.addAttribute("routes", routes))
                            .thenReturn("routes/index");
                })
                .onErrorResume(e -> {
                    log.error("Error deleting route", e);
                    model.addAttribute("error", "Error deleting route: " + e.getMessage());
                    return dynamicRouteService.getAllRoutes()
                            .collectList()
                            .doOnNext(routes -> model.addAttribute("routes", routes))
                            .thenReturn("routes/index");
                });
    }

    @GetMapping("/routes/view/{id}")
    public Mono<String> viewRoute(@PathVariable String id, Model model) {
        return dynamicRouteService.getRouteById(id)
                .flatMap(route -> {
                    model.addAttribute("route", route);
                    return Mono.just("routes/route-details");
                })
                .switchIfEmpty(Mono.defer(() -> {
                    model.addAttribute("error", "Route not found: " + id);
                    return dynamicRouteService.getAllRoutes()
                            .collectList()
                            .doOnNext(routes -> model.addAttribute("routes", routes))
                            .thenReturn("routes/index");
                }));
    }

    @GetMapping
    public String adminHome() {
        return "redirect:/admin/routes";
    }

    @GetMapping("/routes/simple")
    public String showSimpleForm(Model model) {
        return "routes/simple-route-form";
    }

    @PostMapping("/routes/simple-save")
    public Mono<String> saveSimpleRoute(SimpleRouteForm form, Model model) {
        try {
            // Build the route
            GatewayRouteDefinition route = buildSimpleRoute(
                form.getRouteId(), 
                form.getTargetUri(), 
                form.getIncomingPath(), 
                form.getRewriteType(), 
                form.getTargetPath(), 
                form.getDescription());
            
            return dynamicRouteService.addRoute(route)
                    .flatMap(result -> {
                        model.addAttribute("success", "Route created successfully: " + form.getRouteId());
                        return dynamicRouteService.getAllRoutes()
                                .collectList()
                                .doOnNext(routes -> model.addAttribute("routes", routes))
                                .thenReturn("routes/index");
                    })
                    .onErrorResume(e -> {
                        log.error("Error creating simple route", e);
                        log.info("error: " + e);
                        model.addAttribute("error", "Error creating route: " + e.getMessage());
                        return Mono.just("routes/simple-route-form");
                    });
        } catch (Exception e) {
            log.error("Error building simple route", e);
                                    log.info("error: " + e);

            model.addAttribute("error", "Error: " + e.getMessage());
            return Mono.just("routes/simple-route-form");
        }
    }

    private GatewayRouteDefinition buildSimpleRoute(String routeId, String targetUri, 
                                                     String incomingPath, String rewriteType, 
                                                     String targetPath, String description) {
        // Normalize paths
        if (!incomingPath.startsWith("/")) {
            incomingPath = "/" + incomingPath;
        }
        
        // Create Path predicate
        GatewayPredicateDefinition pathPredicate = new GatewayPredicateDefinition();
        pathPredicate.setName("Path");
        Map<String, String> pathArgs = new LinkedHashMap<>();
        pathArgs.put("_genkey_0", incomingPath + "/**");
        pathPredicate.setArgs(pathArgs);

        List<GatewayPredicateDefinition> predicates = new ArrayList<>();
        predicates.add(pathPredicate);

        // Create filters based on rewrite type
        List<GatewayFilterDefinition> filters = new ArrayList<>();
        
        if ("strip".equals(rewriteType)) {
            // StripPrefix filter
            GatewayFilterDefinition stripFilter = new GatewayFilterDefinition();
            stripFilter.setName("StripPrefix");
            Map<String, String> stripArgs = new LinkedHashMap<>();
            stripArgs.put("_genkey_0", "1");
            stripFilter.setArgs(stripArgs);
            filters.add(stripFilter);
        } else if ("rewrite".equals(rewriteType) && targetPath != null && !targetPath.trim().isEmpty()) {
            // RewritePath filter
            if (!targetPath.startsWith("/")) {
                targetPath = "/" + targetPath;
            }
            GatewayFilterDefinition rewriteFilter = new GatewayFilterDefinition();
            rewriteFilter.setName("RewritePath");
            Map<String, String> rewriteArgs = new LinkedHashMap<>();
            rewriteArgs.put("_genkey_0", incomingPath + "/(?<segment>.*)");
            rewriteArgs.put("_genkey_1", targetPath + "/${segment}");
            rewriteFilter.setArgs(rewriteArgs);
            filters.add(rewriteFilter);
        }

        return GatewayRouteDefinition.builder()
                .id(routeId)
                .uri(targetUri)
                .predicates(predicates)
                .filters(filters)
                .order(0)
                .description(description)
                .build();
    }
}
