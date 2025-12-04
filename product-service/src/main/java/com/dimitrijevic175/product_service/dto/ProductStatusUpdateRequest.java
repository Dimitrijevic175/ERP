package com.dimitrijevic175.product_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductStatusUpdateRequest {
    @NotNull(message = "Active status must be provided")
    private Boolean active;
}

