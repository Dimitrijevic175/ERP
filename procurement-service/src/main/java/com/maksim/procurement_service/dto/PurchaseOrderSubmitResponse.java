package com.maksim.procurement_service.dto;

public record PurchaseOrderSubmitResponse(
        Long purchaseOrderId,
        String supplierEmail,
        byte[] pdfBytes
) {}
