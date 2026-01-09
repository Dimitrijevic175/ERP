package com.maksim.procurement_service.dto.supplier;

import lombok.Data;

@Data
public class SupplierCreateRequestDto {
    private String name;
    private String address;
    private String taxNumber;
    private String registrationNumber;
}
