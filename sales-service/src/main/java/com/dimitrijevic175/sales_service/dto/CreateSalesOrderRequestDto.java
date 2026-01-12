package com.dimitrijevic175.sales_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateSalesOrderRequestDto {

    private Long customerId;
    private List<CreateSalesOrderItemDto> items;

    @Data
    public static class CreateSalesOrderItemDto {
        private Long productId;
        private Integer quantity;
        private BigDecimal discount; // 10 = 10%
        private BigDecimal taxRate;  // npr 20%
        private BigDecimal sellingPrice;
    }
}
