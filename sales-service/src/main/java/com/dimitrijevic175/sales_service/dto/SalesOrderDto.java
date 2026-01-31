package com.dimitrijevic175.sales_service.dto;

import com.dimitrijevic175.sales_service.domain.SalesOrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SalesOrderDto {
    private Long id;
    private Long customerId;
    private SalesOrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private BigDecimal totalAmount;
    private List<SalesOrderItemDto> items;
}
