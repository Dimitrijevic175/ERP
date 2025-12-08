package com.dimitrijevic175.product_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class IncreaseStockRequest {

    @NotNull
    @Min(1)
    private Integer amount;
}
