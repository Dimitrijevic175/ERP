package com.maksim.procurement_service.service;

import com.maksim.procurement_service.dto.CreatePurchaseOrderRequestDto;
import com.maksim.procurement_service.dto.PurchaseOrderResponseDto;

public interface PurchaseOrderService {

    PurchaseOrderResponseDto createAutoPurchaseOrder(CreatePurchaseOrderRequestDto request);
}
