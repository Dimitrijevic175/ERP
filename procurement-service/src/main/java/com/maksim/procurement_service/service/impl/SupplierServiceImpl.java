package com.maksim.procurement_service.service.impl;

import com.maksim.procurement_service.domain.Supplier;
import com.maksim.procurement_service.dto.supplier.*;
import com.maksim.procurement_service.mapper.SupplierMapper;
import com.maksim.procurement_service.repository.SupplierRepository;
import com.maksim.procurement_service.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    public SupplierResponseDto createSupplier(SupplierCreateRequestDto request) {
        Supplier supplier = supplierMapper.toEntity(request);
        return supplierMapper.toDto(
                supplierRepository.save(supplier)
        );
    }

    @Override
    public List<SupplierResponseDto> getAllSuppliers() {
        return supplierRepository.findAll()
                .stream()
                .map(supplierMapper::toDto)
                .toList();
    }

    @Override
    public SupplierResponseDto getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        return supplierMapper.toDto(supplier);
    }

    @Override
    public SupplierResponseDto updateSupplier(Long id, SupplierUpdateRequestDto request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        supplierMapper.updateEntity(supplier, request);

        return supplierMapper.toDto(
                supplierRepository.save(supplier)
        );
    }

    @Override
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        supplierRepository.delete(supplier);
    }
}
