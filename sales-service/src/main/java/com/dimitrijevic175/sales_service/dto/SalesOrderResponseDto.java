package com.dimitrijevic175.sales_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SalesOrderResponseDto {

    private Long id;
    private Long warehouseId;
    private Long customerId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private List<SalesOrderItemDto> items;

    @Data
    public static class SalesOrderItemDto {
        private Long productId;
        private Integer quantity;
        private BigDecimal discount;
        private BigDecimal taxRate;
        private BigDecimal sellingPrice;
    }
}
