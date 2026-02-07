package com.maksim.procurement_service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${product.service.url}")
    private String productServiceUrl;

    @Value("${warehouse.service.url}")
    private String warehouseServiceUrl;

    @Bean
    public WebClient warehouseWebClient() {
        return WebClient.builder()
                .baseUrl(warehouseServiceUrl)
                .build();
    }

    @Bean
    public WebClient productWebClient() {
        return WebClient.builder()
                .baseUrl(productServiceUrl)
                .build();
    }

    @Bean
    public WebClient notificationWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8087/api")
                .build();
    }
}
