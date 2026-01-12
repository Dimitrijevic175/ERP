package com.dimitrijevic175.sales_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient warehouseWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8084")
                .build();
    }


}
