package com.maksim.procurement_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseOrderDto {
    private Long id;
    private List<PurchaseOrderItemDto> items;
}
