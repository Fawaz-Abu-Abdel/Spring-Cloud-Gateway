package com.doors.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleRouteForm {
    private String routeId;
    private String targetUri;
    private String incomingPath;
    private String rewriteType;
    private String targetPath;
    private String description;
}
