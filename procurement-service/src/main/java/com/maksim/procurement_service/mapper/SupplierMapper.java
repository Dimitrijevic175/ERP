package com.maksim.procurement_service.mapper;

import com.maksim.procurement_service.domain.Supplier;
import com.maksim.procurement_service.dto.supplier.*;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    public Supplier toEntity(SupplierCreateRequestDto dto) {
        Supplier supplier = new Supplier();
        supplier.setName(dto.getName());
        supplier.setAddress(dto.getAddress());
        supplier.setTaxNumber(dto.getTaxNumber());
        supplier.setRegistrationNumber(dto.getRegistrationNumber());
        supplier.setActive(true);
        return supplier;
    }

    public void updateEntity(Supplier supplier, SupplierUpdateRequestDto dto) {
        supplier.setName(dto.getName());
        supplier.setAddress(dto.getAddress());
        supplier.setActive(dto.isActive());
    }

    public SupplierResponseDto toDto(Supplier supplier) {
        SupplierResponseDto dto = new SupplierResponseDto();
        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        dto.setAddress(supplier.getAddress());
        dto.setTaxNumber(supplier.getTaxNumber());
        dto.setRegistrationNumber(supplier.getRegistrationNumber());
        dto.setActive(supplier.isActive());
        return dto;
    }
}
