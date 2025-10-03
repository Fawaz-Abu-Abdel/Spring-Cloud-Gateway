package com.doors.gateway.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayRouteDefinition {
    
    @NotBlank(message = "Route ID is required")
    private String id;
    
    @NotBlank(message = "URI is required")
    private String uri;
    
    @Builder.Default
    private List<GatewayPredicateDefinition> predicates = new ArrayList<>();
    
    @Builder.Default
    private List<GatewayFilterDefinition> filters = new ArrayList<>();
    
    private Integer order;
    
    private String description;
}
