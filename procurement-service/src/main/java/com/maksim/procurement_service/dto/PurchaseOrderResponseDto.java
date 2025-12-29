package com.maksim.procurement_service.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseOrderResponseDto {
    private Long id;
    private Long warehouseId;
    private Long supplierId;
    private String supplierName;
    private String status;
    private LocalDateTime createdAt;
    private List<PurchaseOrderItemDto> items;
}
