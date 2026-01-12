package com.dimitrijevic175.warehouse_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class CheckWarehouseAvailabilityRequestDto {
    private List<ProductQuantity> items;

    @Data
    public static class ProductQuantity {
        private Long productId;
        private Integer quantity;
    }
}
