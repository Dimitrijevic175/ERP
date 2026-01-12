package com.dimitrijevic175.warehouse_service.service;

import com.dimitrijevic175.warehouse_service.domain.WarehouseStock;
import com.dimitrijevic175.warehouse_service.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseService {

    // Low stock za jedan magacin
    List<LowStockItemDto> getLowStockByWarehouse(Long warehouseId);
    // Low stock za sve magacine (sumirano po proizvodu)
    // Low stock globalno (sumirano po proizvodu)
    List<LowStockItemDto> getLowStockGlobal();

    // nova metoda
    WarehouseDto getWarehouseById(Long warehouseId);
    Page<WarehouseDto> getAllWarehouses(Pageable pageable);
    WarehouseDto updateWarehouse(Long id, WarehouseUpdateRequestDto request);
    void deleteWarehouse(Long id);

    CheckWarehouseAvailabilityResponseDto findWarehouseForOrder(CheckWarehouseAvailabilityRequestDto request);



}