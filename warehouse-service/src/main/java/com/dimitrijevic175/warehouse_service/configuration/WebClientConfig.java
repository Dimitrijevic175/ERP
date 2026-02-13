package com.dimitrijevic175.warehouse_service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${product.service.url}")
    String productUrl;
    @Value("${procurement.service.url}")
    String procurementUrl;

    @Bean
    public WebClient productWebClient() {
        return WebClient.builder()
                .baseUrl(productUrl)
                .build();
    }

    @Bean
    public WebClient procurementWebClient() {
        return WebClient.builder()
                .baseUrl(procurementUrl)
                .build();
    }

}
