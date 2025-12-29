package com.maksim.procurement_service.dto;

import lombok.Data;

@Data
public class CreatePurchaseOrderRequestDto {
    private Long warehouseId;
    private Long supplierId;
}
