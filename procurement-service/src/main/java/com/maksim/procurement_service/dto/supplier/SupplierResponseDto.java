package com.maksim.procurement_service.dto.supplier;

import lombok.Data;

@Data
public class SupplierResponseDto {
    private Long id;
    private String name;
    private String address;
    private String taxNumber;
    private String registrationNumber;
    private boolean active;
}
