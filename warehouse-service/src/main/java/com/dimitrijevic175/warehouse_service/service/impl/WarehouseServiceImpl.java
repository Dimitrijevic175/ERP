package com.dimitrijevic175.warehouse_service.service.impl;

import com.dimitrijevic175.warehouse_service.domain.Warehouse;
import com.dimitrijevic175.warehouse_service.domain.WarehouseStock;
import com.dimitrijevic175.warehouse_service.dto.LowStockItemDto;
import com.dimitrijevic175.warehouse_service.repository.WarehouseRepository;
import com.dimitrijevic175.warehouse_service.repository.WarehouseStockRepository;
import com.dimitrijevic175.warehouse_service.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseStockRepository stockRepository;
    private final WebClient productWebClient; // WebClient za sinhroni poziv Product service
    private final WarehouseRepository warehouseRepository;
    // -------------------------------
    // 1. Low stock po jednom magacinu
    // -------------------------------
    @Override
    public List<LowStockItemDto> getLowStockByWarehouse(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        return warehouse.getStock().stream()
                .map(stock -> {
                    Integer minQuantity = getProductMinQuantity(stock.getProductId());

                    if (minQuantity != null && stock.getQuantity() <= minQuantity) {
                        LowStockItemDto dto = new LowStockItemDto();
                        dto.setWarehouseStockId(stock.getId());
                        dto.setProductId(stock.getProductId());
                        dto.setQuantity(stock.getQuantity());
                        dto.setMinQuantity(minQuantity);
                        dto.setWarehouseId(warehouse.getId());
                        dto.setWarehouseName(warehouse.getName());
                        return dto;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }


    // -------------------------------
    // 2. Low stock globalno (sumirano)
    // -------------------------------
    @Override
    public List<LowStockItemDto> getLowStockGlobal() {
        List<WarehouseStock> allStocks = stockRepository.findAll();

        // 1. Sumiranje quantity po productId
        Map<Long, Integer> totalQuantityMap = new HashMap<>();

        for (WarehouseStock stock : allStocks) {
            totalQuantityMap.merge(
                    stock.getProductId(),
                    stock.getQuantity(),
                    Integer::sum
            );
        }

        // 2. Provera minQuantity + mapiranje u DTO
        List<LowStockItemDto> result = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : totalQuantityMap.entrySet()) {
            Long productId = entry.getKey();
            Integer totalQty = entry.getValue();

            Integer minQty = getProductMinQuantity(productId);

            if (minQty != null && totalQty <= minQty) {
                LowStockItemDto dto = new LowStockItemDto();
                dto.setProductId(productId);
                dto.setQuantity(totalQty);
                dto.setMinQuantity(minQty);

                result.add(dto);
            }
        }
        return result;
    }

    // -------------------------------
    // Helper: proverava da li je quantity manji ili jednak od minQuantity
    // -------------------------------
    private boolean isLowStock(Long productId, Integer quantity) {
        Integer minQuantity = getProductMinQuantity(productId);
        return minQuantity != null && quantity <= minQuantity;
    }

    // -------------------------------
    // Helper: sinhroni poziv product service za minQuantity
    // -------------------------------
    private Integer getProductMinQuantity(Long productId) {
        try {
            return productWebClient.get()
                    .uri("/product/{id}/minQuantity", productId)
                    .retrieve()
                    .bodyToMono(Integer.class)
                    .block(); // sinhrono
        } catch (Exception e) {
            // Možeš logovati ili baciti runtime exception po potrebi
            System.err.println("Failed to fetch minQuantity for productId=" + productId + ": " + e.getMessage());
            return null;
        }
    }
}
