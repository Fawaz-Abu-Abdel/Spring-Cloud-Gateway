package com.doors.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayPredicateDefinition {
    
    private String name;
    
    @Builder.Default
    private Map<String, String> args = new LinkedHashMap<>();
}
