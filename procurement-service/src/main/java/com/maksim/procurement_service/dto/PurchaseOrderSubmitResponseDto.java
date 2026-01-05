package com.maksim.procurement_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PurchaseOrderSubmitResponseDto {
    private Long purchaseOrderId;
    private String status;
    private LocalDateTime submittedAt;
    private String pdfPath; // putanja gde je PDF sačuvan
}
