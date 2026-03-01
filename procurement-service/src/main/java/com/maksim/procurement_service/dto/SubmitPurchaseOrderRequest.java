package com.maksim.procurement_service.dto;

import lombok.Data;

import java.util.List;
@Data
public class SubmitPurchaseOrderRequest {

    private List<PurchaseOrderItemDto> items;

}
