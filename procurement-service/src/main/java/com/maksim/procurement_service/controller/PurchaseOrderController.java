package com.maksim.procurement_service.controller;

import com.maksim.procurement_service.dto.*;
import com.maksim.procurement_service.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<PurchaseOrderResponseDto> createAutoPurchaseOrder(
            @RequestBody CreatePurchaseOrderRequestDto request
    ) {
        PurchaseOrderResponseDto response = purchaseOrderService.createAutoPurchaseOrder(request);
        return ResponseEntity.ok(response);
    }
}
