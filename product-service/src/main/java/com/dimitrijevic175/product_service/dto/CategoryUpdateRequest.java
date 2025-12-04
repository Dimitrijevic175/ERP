package com.dimitrijevic175.product_service.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryUpdateRequest {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters.")
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters.")
    private String description;

    @Positive(message = "Parent category ID must be a positive number.")
    private Long parentCategoryId;

    private Boolean active;
}
