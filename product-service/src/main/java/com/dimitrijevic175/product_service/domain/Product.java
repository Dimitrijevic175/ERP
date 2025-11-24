package com.dimitrijevic175.product_service.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String sku;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String brand;

    private BigDecimal purchasePrice;

    private BigDecimal sellingPrice;

    private BigDecimal taxRate;

    private Integer quantity;

    private Integer minQuantity;

    private Integer maxQuantity;

    private String unitOfMeasure;

    private Boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();


}
