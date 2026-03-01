package com.maksim.procurement_service.service;

import com.maksim.procurement_service.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PurchaseOrderService {
    Page<PurchaseOrderDto> getAllPurchaseOrders(Pageable pageable);
    PurchaseOrderResponseDto createAutoPurchaseOrder(CreatePurchaseOrderRequestDto request);
    // Submit PO i generisanje PDF
    PurchaseOrderSubmitResponse submitPurchaseOrder(Long purchaseOrderId, SubmitPurchaseOrderRequest request);
    String confirmPurchaseOrder(Long purchaseOrderId);
    String closePurchaseOrder(Long purchaseOrderId);
    void receivePurchaseOrder(Long id);
    PurchaseOrderDto getPurchaseOrderById(Long id);

}
