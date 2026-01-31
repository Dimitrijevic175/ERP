package com.maksim.procurement_service.service.impl;

import com.maksim.procurement_service.domain.Supplier;
import com.maksim.procurement_service.dto.supplier.*;
import com.maksim.procurement_service.mapper.SupplierMapper;
import com.maksim.procurement_service.repository.SupplierRepository;
import com.maksim.procurement_service.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private static final Logger logger = LogManager.getLogger(SupplierServiceImpl.class);


    @Override
    public SupplierResponseDto createSupplier(SupplierCreateRequestDto request) {
        logger.info("Creating new supplier with name={}", request.getName());

        Supplier supplier = supplierMapper.toEntity(request);
        Supplier saved = supplierRepository.save(supplier);

        logger.debug("Supplier created successfully with id={}", saved.getId());
        return supplierMapper.toDto(saved);
    }

    @Override
    public List<SupplierResponseDto> getAllSuppliers() {
        logger.info("Fetching all suppliers");
        List<SupplierResponseDto> suppliers = supplierRepository.findAll()
                .stream()
                .map(supplierMapper::toDto)
                .toList();
        logger.debug("Fetched {} suppliers", suppliers.size());
        return suppliers;
    }

    @Override
    public SupplierResponseDto getSupplierById(Long id) {
        logger.info("Fetching supplier with id={}", id);
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Supplier not found with id={}", id);
                    return new RuntimeException("Supplier not found");
                });
        logger.debug("Supplier fetched: id={}, name={}", supplier.getId(), supplier.getName());
        return supplierMapper.toDto(supplier);
    }

    @Override
    public SupplierResponseDto updateSupplier(Long id, SupplierUpdateRequestDto request) {
        logger.info("Updating supplier with id={}", id);
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Supplier not found for update, id={}", id);
                    return new RuntimeException("Supplier not found");
                });

        supplierMapper.updateEntity(supplier, request);
        Supplier updated = supplierRepository.save(supplier);
        logger.debug("Supplier updated successfully: id={}, name={}", updated.getId(), updated.getName());

        return supplierMapper.toDto(updated);
    }

    @Override
    public void deleteSupplier(Long id) {
        logger.info("Deleting supplier with id={}", id);
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Supplier not found for deletion, id={}", id);
                    return new RuntimeException("Supplier not found");
                });

        supplierRepository.delete(supplier);
        logger.debug("Supplier deleted successfully: id={}, name={}", supplier.getId(), supplier.getName());
    }
}
