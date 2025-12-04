package com.dimitrijevic175.product_service.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class ProductStockResponse {
    private Long productId;
    private Integer quantity;
}
