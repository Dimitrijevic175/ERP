package com.dimitrijevic175.warehouse_service.controller;

import com.dimitrijevic175.warehouse_service.domain.WarehouseStock;
import com.dimitrijevic175.warehouse_service.dto.*;
import com.dimitrijevic175.warehouse_service.repository.WarehouseStockRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import com.dimitrijevic175.warehouse_service.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final WarehouseStockRepository warehouseStockRepository;

    @GetMapping("/{id}/stock")
    public ResponseEntity<List<WarehouseStockDto>> getWarehouseStock(@PathVariable Long id) {
        List<WarehouseStock> stockList = warehouseStockRepository.findByWarehouseId(id);

        List<WarehouseStockDto> dtos = stockList.stream()
                .map(stock -> {
                    WarehouseStockDto dto = new WarehouseStockDto();
                    dto.setId(stock.getId());
                    dto.setProductId(stock.getProductId());
                    dto.setQuantity(stock.getQuantity());
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(dtos);
    }


    @GetMapping
    public ResponseEntity<Page<WarehouseDto>> getAllWarehouses(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                warehouseService.getAllWarehouses(pageable)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseDto> updateWarehouse(@PathVariable Long id, @RequestBody WarehouseUpdateRequestDto request)
    {
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}/lowStock")
    public ResponseEntity<List<LowStockItemDto>> lowStock(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getLowStockByWarehouse(id));
    }

    @GetMapping("/lowStockGlobal")
    public ResponseEntity<List<LowStockItemDto>> getLowStockGlobal() {
        return ResponseEntity.ok(warehouseService.getLowStockGlobal());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseDto> getWarehouse(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getWarehouseById(id));
    }

    @PostMapping("/checkAvailability")
    public ResponseEntity<CheckWarehouseAvailabilityResponseDto> checkAvailability(
            @RequestBody CheckWarehouseAvailabilityRequestDto request
    ) {
        CheckWarehouseAvailabilityResponseDto response = warehouseService.findWarehouseForOrder(request);
        return ResponseEntity.ok(response);
    }
}
