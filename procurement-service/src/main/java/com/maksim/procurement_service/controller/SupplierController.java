package com.maksim.procurement_service.controller;

import com.maksim.procurement_service.dto.supplier.*;
import com.maksim.procurement_service.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<SupplierResponseDto> createSupplier(
            @RequestBody SupplierCreateRequestDto request
    ) {
        return ResponseEntity.ok(
                supplierService.createSupplier(request)
        );
    }

    @GetMapping
    public ResponseEntity<List<SupplierResponseDto>> getAllSuppliers() {
        return ResponseEntity.ok(
                supplierService.getAllSuppliers()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDto> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(
                supplierService.getSupplierById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponseDto> updateSupplier(
            @PathVariable Long id,
            @RequestBody SupplierUpdateRequestDto request
    ) {
        return ResponseEntity.ok(
                supplierService.updateSupplier(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}
