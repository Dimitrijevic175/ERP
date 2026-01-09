package com.maksim.procurement_service.mapper;

import com.maksim.procurement_service.domain.PurchaseOrder;
import com.maksim.procurement_service.dto.PurchaseOrderDto;
import org.springframework.stereotype.Component;

@Component
public class PurchaseOrderMapper {

    public PurchaseOrderDto toDto(PurchaseOrder po) {
        PurchaseOrderDto dto = new PurchaseOrderDto();
        dto.setId(po.getId());
        dto.setStatus(po.getStatus());
        dto.setCreatedAt(po.getCreatedAt());
        dto.setSupplierId(po.getSupplier().getId());
        return dto;
    }
}
