package com.dimitrijevic175.warehouse_service.dto;

import lombok.Data;

@Data
public class LowStockItemDto {

    private Long warehouseStockId;
    private Long productId;
    private Integer quantity;
    private Integer minQuantity;
    private Long warehouseId;
    private String warehouseName;


}

