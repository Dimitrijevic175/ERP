package com.dimitrijevic175.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCategoryRequest {
    @NotBlank(message = "Name is required.")
    private String name;
    private String description;
    private Long parentCategoryId;
}
