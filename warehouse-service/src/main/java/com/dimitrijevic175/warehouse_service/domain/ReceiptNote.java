package com.dimitrijevic175.warehouse_service.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "receipt_notes")
public class ReceiptNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "purchase_order_id", nullable = false)
    private Long purchaseOrderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReceiptNoteStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @OneToMany(mappedBy = "receiptNote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceiptNoteItem> items = new ArrayList<>();
}
