package com.dimitrijevic175.sales_service.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${warehouse.service.url}")
    private String warehouseUrl;
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
                .baseUrl(warehouseUrl)
                .build();
    }


}
