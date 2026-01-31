package com.dimitrijevic175.sales_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalesOrderItemDto {
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
}
