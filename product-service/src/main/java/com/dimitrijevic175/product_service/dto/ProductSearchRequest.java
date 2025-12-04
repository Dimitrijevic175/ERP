package com.dimitrijevic175.product_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductSearchRequest {

    private String name;
    private String sku;
    private Long categoryId;
    private String brand;

    private BigDecimal priceMin;
    private BigDecimal priceMax;

    private Boolean active;
}
