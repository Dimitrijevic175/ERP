package com.maksim.procurement_service.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${product.service.url}")
    private String productServiceUrl;

    @Value("${warehouse.service.url}")
    private String warehouseServiceUrl;

    private final HttpServletRequest request;

    public WebClientConfig(HttpServletRequest request) {
        this.request = request;
    }


    private WebClient.Builder withJwtFilter(WebClient.Builder builder) {
        return builder.filter((clientRequest, next) -> {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null) {
                ClientRequest filtered = ClientRequest.from(clientRequest)
                        .header("Authorization", authHeader)
                        .build();
                return next.exchange(filtered);
            }
            return next.exchange(clientRequest);
        });
    }

    @Bean
    public WebClient warehouseWebClient() {
        return withJwtFilter(WebClient.builder())
                .baseUrl(warehouseServiceUrl)
                .build();
    }

    @Bean
    public WebClient productWebClient() {
        return withJwtFilter(WebClient.builder())
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
