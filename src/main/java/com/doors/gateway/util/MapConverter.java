package com.doors.gateway.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapConverter {

    /**
     * Convert Map to multi-line string format (key=value per line)
     */
    public String mapToString(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * Convert multi-line string to Map (one key=value per line)
     */
    public Map<String, String> stringToMap(String source) {
        Map<String, String> map = new LinkedHashMap<>();
        if (source != null && !source.trim().isEmpty()) {
            String[] lines = source.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (!line.isEmpty() && line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        map.put(parts[0].trim(), parts[1].trim());
                    }
                }
            }
        }
        return map;
    }
}
