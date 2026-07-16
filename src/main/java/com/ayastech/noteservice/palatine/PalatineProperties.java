package com.ayastech.noteservice.palatine;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "palatine")
public record PalatineProperties(
        String baseUrl,
        String model,
        String token
) {
}
