package com.dimitrijevic175.warehouse_service.mapper;

import com.dimitrijevic175.warehouse_service.domain.Warehouse;
import com.dimitrijevic175.warehouse_service.dto.WarehouseDto;
import com.dimitrijevic175.warehouse_service.dto.WarehouseUpdateRequestDto;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMapper {

    public WarehouseDto toDto(Warehouse warehouse) {
        WarehouseDto dto = new WarehouseDto();
        dto.setId(warehouse.getId());
        dto.setName(warehouse.getName());
        dto.setLocation(warehouse.getLocation());
        return dto;
    }

    public void updateEntity(Warehouse warehouse, WarehouseUpdateRequestDto dto) {
        warehouse.setName(dto.getName());
        warehouse.setLocation(dto.getLocation());
    }
}
