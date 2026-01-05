package com.dimitrijevic175.warehouse_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseOrderDto {
    private Long id;
    private List<PurchaseOrderItemDto> items;
}
