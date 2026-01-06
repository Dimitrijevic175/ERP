package com.maksim.procurement_service.service;

import com.maksim.procurement_service.dto.CreatePurchaseOrderRequestDto;
import com.maksim.procurement_service.dto.PurchaseOrderDto;
import com.maksim.procurement_service.dto.PurchaseOrderResponseDto;
import com.maksim.procurement_service.dto.PurchaseOrderSubmitResponseDto;

public interface PurchaseOrderService {

    PurchaseOrderResponseDto createAutoPurchaseOrder(CreatePurchaseOrderRequestDto request);
    // Submit PO i generisanje PDF
    String submitPurchaseOrder(Long purchaseOrderId);
    String confirmPurchaseOrder(Long purchaseOrderId);
    String closePurchaseOrder(Long purchaseOrderId);
    void receivePurchaseOrder(Long id);
    PurchaseOrderDto getPurchaseOrderById(Long id);

}
