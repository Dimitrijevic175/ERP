package com.maksim.procurement_service.dto;

import com.maksim.procurement_service.domain.PurchaseOrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseOrderDto {
    private Long id;
    private List<PurchaseOrderItemDto> items;
    private PurchaseOrderStatus status;
    private LocalDateTime createdAt;
    private Long supplierId;
    private Long warehouseId;
    private String supplierName;
    private String warehouseName;
}
