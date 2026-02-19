package com.maksim.procurement_service.mapper;

import com.maksim.procurement_service.configuration.ProductClient;
import com.maksim.procurement_service.domain.PurchaseOrder;
import com.maksim.procurement_service.domain.PurchaseOrderItem;
import com.maksim.procurement_service.dto.PurchaseOrderDto;
import com.maksim.procurement_service.dto.PurchaseOrderItemDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PurchaseOrderMapper {

    private final ProductClient productClient;

    public PurchaseOrderMapper(ProductClient productClient) {
        this.productClient = productClient;
    }

    public PurchaseOrderDto toDto(PurchaseOrder po) {
        PurchaseOrderDto dto = new PurchaseOrderDto();
        dto.setId(po.getId());
        dto.setStatus(po.getStatus());
        dto.setCreatedAt(po.getCreatedAt());
        dto.setSupplierId(po.getSupplier().getId());

        if (po.getItems() != null) {
            List<PurchaseOrderItemDto> items = po.getItems().stream()
                    .map(item -> {
                        PurchaseOrderItemDto dtoItem = new PurchaseOrderItemDto();
                        dtoItem.setProductId(item.getProductId());
                        dtoItem.setQuantity(item.getQuantity());
                        dtoItem.setPurchasePrice(item.getPurchasePrice());

                        // Dohvatanje imena proizvoda sa product-service
                        var product = productClient.getProductById(item.getProductId());
                        if (product != null) {
                            dtoItem.setProductName(product.getName());
                        } else {
                            dtoItem.setProductName("Unknown Product");
                        }

                        return dtoItem;
                    })
                    .collect(Collectors.toList());

            dto.setItems(items);
        }

        return dto;
    }
}
