package com.maksim.procurement_service.service;


import com.maksim.procurement_service.dto.supplier.*;

import java.util.List;

public interface SupplierService {

    SupplierResponseDto createSupplier(SupplierCreateRequestDto request);

    List<SupplierResponseDto> getAllSuppliers();

    SupplierResponseDto getSupplierById(Long id);

    SupplierResponseDto updateSupplier(Long id, SupplierUpdateRequestDto request);

    void deleteSupplier(Long id);
}
