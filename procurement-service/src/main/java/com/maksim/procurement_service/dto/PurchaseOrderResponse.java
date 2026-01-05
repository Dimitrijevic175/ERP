package com.maksim.procurement_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseOrderResponse {
    private Long id;
    private Long warehouseId;
    private Long supplierId;
    private String status;
    private LocalDateTime createdAt;
    private List<PurchaseOrderItemResponse> items;
}
