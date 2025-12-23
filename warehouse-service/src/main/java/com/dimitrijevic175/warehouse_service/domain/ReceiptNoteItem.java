package com.dimitrijevic175.warehouse_service.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "receipt_note_items")
public class ReceiptNoteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_note_id", nullable = false)
    private ReceiptNote receiptNote;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "ordered_quantity", nullable = false)
    private Integer orderedQuantity;

    @Column(name = "received_quantity", nullable = false)
    private Integer receivedQuantity;

    @Column(name = "purchase_price", precision = 19, scale = 4, nullable = false)
    private BigDecimal purchasePrice;
}
