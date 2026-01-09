package com.maksim.procurement_service.dto.supplier;

import lombok.Data;

@Data
public class SupplierUpdateRequestDto {
    private String name;
    private String address;
    private boolean active;
}
