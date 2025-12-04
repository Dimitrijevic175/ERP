package com.dimitrijevic175.product_service.dto;

import lombok.*;

@Data
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private Long parentCategoryId;
    private Boolean active;
}
