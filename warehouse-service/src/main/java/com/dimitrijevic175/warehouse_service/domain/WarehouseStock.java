package com.dimitrijevic175.warehouse_service.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "warehouse_stock")
public class WarehouseStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;
}
