package com.dimitrijevic175.warehouse_service.controller;

import com.dimitrijevic175.warehouse_service.domain.WarehouseStock;
import com.dimitrijevic175.warehouse_service.dto.LowStockItemDto;
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

    @GetMapping("/{id}/lowStock")
    public ResponseEntity<List<LowStockItemDto>> lowStock(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getLowStockByWarehouse(id));
    }

    @GetMapping("/lowStockGlobal")
    public ResponseEntity<List<LowStockItemDto>> getLowStockGlobal() {
        return ResponseEntity.ok(warehouseService.getLowStockGlobal());
    }

}
