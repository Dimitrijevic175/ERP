package com.dimitrijevic175.sales_service.dto;

import lombok.Data;

@Data
public class CheckAvailabilityResponseDto {
    private Long warehouseId;
    private String name; // ako postoji ime skladišta
}
