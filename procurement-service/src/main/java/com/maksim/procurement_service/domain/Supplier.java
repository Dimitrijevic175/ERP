package com.maksim.procurement_service.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

@Data
@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, unique = true)
    private String taxNumber; // PIB

    @Column(nullable = false, unique = true)
    private String registrationNumber; // Matični broj

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(
            mappedBy = "supplier",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<SupplierContact> contacts = new ArrayList<>();
}
