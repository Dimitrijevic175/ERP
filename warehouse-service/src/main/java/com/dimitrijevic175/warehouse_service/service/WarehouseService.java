package com.dimitrijevic175.warehouse_service.service;

import com.dimitrijevic175.warehouse_service.domain.WarehouseStock;
import com.dimitrijevic175.warehouse_service.dto.LowStockItemDto;

import java.util.List;

public interface WarehouseService {

    // Low stock za jedan magacin
    List<LowStockItemDto> getLowStockByWarehouse(Long warehouseId);
    // Low stock za sve magacine (sumirano po proizvodu)
    // Low stock globalno (sumirano po proizvodu)
    List<LowStockItemDto> getLowStockGlobal();
}