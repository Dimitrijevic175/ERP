package com.maksim.procurement_service.dto;

import lombok.Data;

@Data
public class LowStockItemDto {
    private Long productId;
    private Integer quantity;      // trenutno stanje u magacinu
    private Integer minQuantity;
    private Integer maxQuantity;   // opciono ako se povuce iz Product service
}
