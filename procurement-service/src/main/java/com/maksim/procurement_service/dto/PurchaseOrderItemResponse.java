package com.maksim.procurement_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseOrderItemResponse {
    private Long productId;
    private Integer quantity;
    private BigDecimal purchasePrice;
}
