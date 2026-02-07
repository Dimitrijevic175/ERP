package com.maksim.procurement_service.configuration;

import com.maksim.procurement_service.dto.ProductDto;
import com.maksim.procurement_service.dto.ProductInfoDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ProductClient {

    private final WebClient productWebClient;

    public ProductClient(WebClient productWebClient) {
        this.productWebClient = productWebClient;
    }

    public ProductInfoDto getProductById(Long productId) {
        return productWebClient.get()
                .uri("/{productId}", productId)
                .retrieve()
                .bodyToMono(ProductInfoDto.class)
                .block();
    }

    // nova metoda za ProductDto
    public ProductDto getFullProductById(Long productId) {
        return productWebClient.get()
                .uri("/{productId}", productId)
                .retrieve()
                .bodyToMono(ProductDto.class)
                .block();
    }
}
