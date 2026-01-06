package com.maksim.procurement_service.controller;

import com.maksim.procurement_service.domain.PurchaseOrder;
import com.maksim.procurement_service.domain.PurchaseOrderStatus;
import com.maksim.procurement_service.dto.*;
import com.maksim.procurement_service.repository.PurchaseOrderRepository;
import com.maksim.procurement_service.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;
    private final PurchaseOrderRepository purchaseOrderRepository;


    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderDto> getPurchaseOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(
                purchaseOrderService.getPurchaseOrderById(id)
        );
    }

    @PostMapping
    public ResponseEntity<PurchaseOrderResponseDto> createAutoPurchaseOrder(
            @RequestBody CreatePurchaseOrderRequestDto request
    ) {
        PurchaseOrderResponseDto response = purchaseOrderService.createAutoPurchaseOrder(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<String> submitPurchaseOrder(@PathVariable Long id) {
        String pdfFile = purchaseOrderService.submitPurchaseOrder(id);
        return ResponseEntity.ok(pdfFile);
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<String> confirmPurchaseOrder(@PathVariable Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));

        if (po.getStatus() != PurchaseOrderStatus.SUBMITTED) {
            return ResponseEntity.badRequest().body("Purchase order cannot be confirmed");
        }

        po.setStatus(PurchaseOrderStatus.CONFIRMED);
        purchaseOrderRepository.save(po);

        return ResponseEntity.ok("Purchase order confirmed successfully");
    }

    @GetMapping("/{id}/close")
    public ResponseEntity<String> closePurchaseOrder(@PathVariable Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));

        if (po.getStatus() != PurchaseOrderStatus.SUBMITTED && po.getStatus()!= PurchaseOrderStatus.RECEIVED) {
            return ResponseEntity.badRequest().body("Purchase order cannot be closed");
        }

        po.setStatus(PurchaseOrderStatus.CLOSED);
        purchaseOrderRepository.save(po);

        return ResponseEntity.ok("Purchase order closed successfully");
    }

    @PostMapping("/{id}/receive")
    public ResponseEntity<String> receivePurchaseOrder(@PathVariable Long id) {
        purchaseOrderService.receivePurchaseOrder(id);
        return ResponseEntity.ok("Purchase order marked as RECEIVED");
    }

}
