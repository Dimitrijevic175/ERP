package com.dimitrijevic175.sales_service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${warehouse.service.url}")
    private String warehouseUrl;
    @Bean
    public WebClient warehouseWebClient() {
        return WebClient.builder()
                .baseUrl(warehouseUrl)
                .build();
    }


}
