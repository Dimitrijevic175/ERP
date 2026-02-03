package com.dimitrijevic175.warehouse_service.dto;

import lombok.Data;

@Data
public class WarehouseStockDto {
    private Long id;
    private Long productId;
    private Integer quantity;
}

