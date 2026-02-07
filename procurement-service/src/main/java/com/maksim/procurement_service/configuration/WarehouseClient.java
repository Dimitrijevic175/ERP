package com.maksim.procurement_service.configuration;

import com.maksim.procurement_service.dto.LowStockItemDto;
import com.maksim.procurement_service.dto.WarehouseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class WarehouseClient {

    private final WebClient warehouseWebClient;

    public WarehouseClient(WebClient warehouseWebClient) {
        this.warehouseWebClient = warehouseWebClient;
    }

    public List<LowStockItemDto> getLowStock(Long warehouseId) {
        return warehouseWebClient.get()
                .uri("/warehouses/{warehouseId}/lowStock", warehouseId)
                .retrieve()
                .bodyToFlux(LowStockItemDto.class)
                .collectList()
                .block();
    }

    public WarehouseDto getWarehouseById(Long warehouseId) {
        return warehouseWebClient.get()
                .uri("/warehouses/{id}", warehouseId)
                .retrieve()
                .bodyToMono(WarehouseDto.class)
                .block();
    }
}
