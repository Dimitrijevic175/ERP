package com.maksim.procurement_service.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "supplier_contacts")
public class SupplierContact {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;
}
