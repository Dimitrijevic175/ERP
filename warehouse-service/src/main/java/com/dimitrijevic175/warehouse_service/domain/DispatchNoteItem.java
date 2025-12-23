package com.dimitrijevic175.warehouse_service.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "dispatch_note_items")
public class DispatchNoteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_note_id", nullable = false)
    private DispatchNote dispatchNote;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "dispatched_quantity", nullable = false)
    private Integer dispatchedQuantity;

    @Column(name = "selling_price", precision = 19, scale = 4, nullable = false)
    private BigDecimal sellingPrice;

    @Column(precision = 5, scale = 2)
    private BigDecimal discount;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate;
}
