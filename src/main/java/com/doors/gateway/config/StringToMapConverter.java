package com.doors.gateway.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class StringToMapConverter implements Converter<String, Map<String, String>> {

    @Override
    public Map<String, String> convert(String source) {
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
