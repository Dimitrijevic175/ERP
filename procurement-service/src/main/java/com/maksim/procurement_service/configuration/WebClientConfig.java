package com.maksim.procurement_service.configuration;

import org.springframework.context.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient warehouseWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8084/warehouses") // URL Warehouse service
                .build();
    }

    @Bean
    public WebClient productWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8081/product") // URL Product service
                .build();
    }

    @Bean
    public WebClient notificationWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8087/api") // URL Product service
                .build();
    }
}
