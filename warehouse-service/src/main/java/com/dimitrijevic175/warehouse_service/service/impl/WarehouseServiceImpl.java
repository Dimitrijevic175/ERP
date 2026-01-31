package com.dimitrijevic175.warehouse_service.service.impl;

import com.dimitrijevic175.warehouse_service.domain.Warehouse;
import com.dimitrijevic175.warehouse_service.domain.WarehouseStock;
import com.dimitrijevic175.warehouse_service.dto.*;
import com.dimitrijevic175.warehouse_service.mapper.WarehouseMapper;
import com.dimitrijevic175.warehouse_service.repository.WarehouseRepository;
import com.dimitrijevic175.warehouse_service.repository.WarehouseStockRepository;
import com.dimitrijevic175.warehouse_service.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final WarehouseMapper warehouseMapper;
    private static final Logger log = LogManager.getLogger(WarehouseServiceImpl.class);

    @Override
    public Page<WarehouseDto> getAllWarehouses(Pageable pageable) {
        log.info("Fetching all warehouses, pageable={}", pageable);
        return warehouseRepository.findAll(pageable)
                .map(warehouseMapper::toDto);
    }

    @Override
    public WarehouseDto updateWarehouse(Long id, WarehouseUpdateRequestDto request) {
        log.info("Updating warehouse with id={}", id);

        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Warehouse not found with id={}", id);
                    return new RuntimeException("Warehouse not found");
                });

        warehouseMapper.updateEntity(warehouse, request);

        Warehouse saved = warehouseRepository.save(warehouse);
        log.info("Warehouse with id={} updated successfully", id);

        return warehouseMapper.toDto(saved);
    }

    @Override
    public void deleteWarehouse(Long id) {
        log.info("Deleting warehouse with id={}", id);

        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Warehouse not found with id={}", id);
                    return new RuntimeException("Warehouse not found");
                });

        warehouseRepository.delete(warehouse);
        log.info("Warehouse with id={} deleted successfully", id);
    }


    // -------------------------------
    // 1. Low stock po jednom magacinu
    // -------------------------------
    @Override
    public List<LowStockItemDto> getLowStockByWarehouse(Long warehouseId) {
        log.debug("Checking low stock for warehouseId={}", warehouseId);

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> {
                    log.error("Warehouse not found with id={}", warehouseId);
                    return new RuntimeException("Warehouse not found");
                });

        List<LowStockItemDto> lowStockItems = warehouse.getStock().stream()
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
                        log.debug("Low stock detected: {}", dto);
                        return dto;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();

        log.info("Found {} low stock items for warehouseId={}", lowStockItems.size(), warehouseId);
        return lowStockItems;
    }


    // -------------------------------
    // 2. Low stock globalno (sumirano)
    // -------------------------------
    @Override
    public List<LowStockItemDto> getLowStockGlobal() {
        log.debug("Checking global low stock");

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
                log.debug("Global low stock detected for productId={}: {}", productId, dto);
            }
        }

        log.info("Global low stock check completed. {} items under minimum.", result.size());
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
            Integer minQty = productWebClient.get()
                    .uri("/product/{id}/minQuantity", productId)
                    .retrieve()
                    .bodyToMono(Integer.class)
                    .block(); // sinhrono
            log.debug("Fetched minQuantity={} for productId={}", minQty, productId);
            return minQty;
        } catch (Exception e) {
            log.error("Failed to fetch minQuantity for productId={}", productId, e);
            return null;
        }
    }

    @Override
    public WarehouseDto getWarehouseById(Long warehouseId) {
        log.debug("Fetching warehouse by id={}", warehouseId);

        Optional<Warehouse> warehouseOpt = warehouseRepository.findById(warehouseId);

        if (warehouseOpt.isEmpty()) {
            log.error("Warehouse not found with id={}", warehouseId);
            throw new RuntimeException("Warehouse not found with id: " + warehouseId);
        }

        Warehouse w = warehouseOpt.get();
        WarehouseDto dto = new WarehouseDto();
        dto.setId(w.getId());
        dto.setName(w.getName());
        dto.setLocation(w.getLocation());

        log.info("Warehouse fetched: {}", dto);
        return dto;
    }

    @Override
    public CheckWarehouseAvailabilityResponseDto findWarehouseForOrder(CheckWarehouseAvailabilityRequestDto request) {
        log.debug("Finding warehouse for order with {} items", request.getItems().size());

        // Dohvati sve magacine
        List<Warehouse> warehouses = warehouseRepository.findAll();

        for (Warehouse warehouse : warehouses) {

            boolean canFulfill = request.getItems().stream().allMatch(orderItem -> {
                return warehouse.getStock().stream()
                        .anyMatch(stock ->
                                stock.getProductId().equals(orderItem.getProductId())
                                        && stock.getQuantity() >= orderItem.getQuantity()
                        );
            });

            if (canFulfill) {
                CheckWarehouseAvailabilityResponseDto response = new CheckWarehouseAvailabilityResponseDto();
                response.setWarehouseId(warehouse.getId());
                log.info("Order can be fulfilled by warehouseId={}", warehouse.getId());
                return response;
            }
        }

        log.warn("No warehouse can fulfill the requested order");
        throw new RuntimeException("No warehouse can fulfill the requested order");
    }

}
