package com.maksim.procurement_service.service.impl;

import com.maksim.procurement_service.domain.*;
import com.maksim.procurement_service.dto.*;
import com.maksim.procurement_service.repository.*;
import com.maksim.procurement_service.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final WebClient warehouseWebClient;
    private final WebClient productWebClient;

    @Override
    public PurchaseOrderResponseDto createAutoPurchaseOrder(CreatePurchaseOrderRequestDto request) {

        // 1. Validacija supplier
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        // 2. Poziv Warehouse servisa za low-stock proizvode
        List<LowStockItemDto> lowStockItems = warehouseWebClient.get()
                .uri("/{warehouseId}/lowStock", request.getWarehouseId())
                .retrieve()
                .bodyToFlux(LowStockItemDto.class)
                .collectList()
                .block();

        // 3. Kreiranje PO
        PurchaseOrder po = new PurchaseOrder();
        po.setWarehouseId(request.getWarehouseId());
        po.setSupplier(supplier);
        po.setStatus(PurchaseOrderStatus.DRAFT);

        // 4. Popunjavanje stavki automatski
        for (LowStockItemDto lowStock : lowStockItems) {

            // 4a. Poziv Product servisa za maxQuantity i purchasePrice
            ProductInfoDto productInfo = productWebClient.get()
                    .uri("/{id}", lowStock.getProductId())
                    .retrieve()
                    .bodyToMono(ProductInfoDto.class)
                    .block();

            if (productInfo == null) continue;

            int orderQuantity = productInfo.getMaxQuantity() - lowStock.getQuantity();
            if (orderQuantity <= 0) continue;

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setPurchaseOrder(po);
            item.setProductId(lowStock.getProductId());
            item.setQuantity(orderQuantity);
            item.setPurchasePrice(productInfo.getPurchasePrice());

            po.getItems().add(item);
        }

        // 5. Snimanje PO
        po = purchaseOrderRepository.save(po);

        // 6. Mapiranje na response DTO
        PurchaseOrderResponseDto response = new PurchaseOrderResponseDto();
        response.setId(po.getId());
        response.setWarehouseId(po.getWarehouseId());
        response.setSupplierId(supplier.getId());
        response.setSupplierName(supplier.getName());
        response.setStatus(po.getStatus().name());
        response.setCreatedAt(po.getCreatedAt());
        response.setItems(po.getItems().stream().map(item -> {
            PurchaseOrderItemDto dto = new PurchaseOrderItemDto();
            dto.setProductId(item.getProductId());
            dto.setQuantity(item.getQuantity());
            dto.setPurchasePrice(item.getPurchasePrice());
            dto.setProductName(productInfoById(item.getProductId(), productWebClient));
            return dto;
        }).collect(Collectors.toList()));

        return response;
    }

    // helper za dohvatanje product name (po potrebi)
    private String productInfoById(Long productId, WebClient productWebClient) {
        ProductInfoDto productInfo = productWebClient.get()
                .uri("/{id}", productId)
                .retrieve()
                .bodyToMono(ProductInfoDto.class)
                .block();
        return productInfo != null ? productInfo.getName() : null;
    }
}
