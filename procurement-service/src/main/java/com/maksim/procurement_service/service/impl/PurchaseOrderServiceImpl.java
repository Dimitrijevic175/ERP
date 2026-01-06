package com.maksim.procurement_service.service.impl;

import com.maksim.procurement_service.domain.*;
import com.maksim.procurement_service.dto.*;
import com.maksim.procurement_service.listener.NotificationSender;
import com.maksim.procurement_service.repository.*;
import com.maksim.procurement_service.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final WebClient warehouseWebClient;
    private final WebClient productWebClient;
    private final NotificationSender notificationSender;

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


    @Override
    @Transactional
    public String submitPurchaseOrder(Long purchaseOrderId) {
        PurchaseOrder po = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));

        if (po.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new RuntimeException("Purchase order already submitted or closed");
        }

        // ----------------------------
        // 1. Popuni stavke sa low stock
        // ----------------------------
        List<LowStockItemDto> lowStockItems = warehouseWebClient.get()
                .uri("/{id}/lowStock", po.getWarehouseId())
                .retrieve()
                .bodyToFlux(LowStockItemDto.class)
                .collectList()
                .block();

        if (lowStockItems != null) {
            for (LowStockItemDto item : lowStockItems) {
                // Ako PurchaseOrder vec sadrzi stavku, preskoci
                boolean exists = po.getItems().stream()
                        .anyMatch(i -> i.getProductId().equals(item.getProductId()));
                if (exists) continue;

                ProductDto product = productWebClient.get()
                        .uri("/{id}", item.getProductId())
                        .retrieve()
                        .bodyToMono(ProductDto.class)
                        .block();

                if (product == null) continue;

                PurchaseOrderItem poItem = new PurchaseOrderItem();
                poItem.setPurchaseOrder(po);
                poItem.setProductId(item.getProductId());
                poItem.setQuantity(product.getMaxQuantity()); // do maxQuantity
                poItem.setPurchasePrice(product.getPurchasePrice());

                po.getItems().add(poItem);
            }
        }

        // ----------------------------
        // 2. Promeni status u SUBMITTED
        // ----------------------------
        po.setStatus(PurchaseOrderStatus.SUBMITTED);
        po.setSubmittedAt(LocalDateTime.now());
        purchaseOrderRepository.save(po);

        // ----------------------------
        // 3. Generisi PDF
        // ----------------------------
        byte[] pdfBytes = generatePurchaseOrderPdfBytes(po);

        // ----------------------------
        // 4. Pripremi HTML telo mejla sa linkovima
        // ----------------------------
        SupplierContact contact = po.getSupplier().getContacts().get(0);

        String baseUrl = "http://localhost:8082/purchase-orders"; // tvoj Procurement service URL
        String confirmLink = baseUrl + "/" + po.getId() + "/confirm";
        String closeLink = baseUrl + "/" + po.getId() + "/close";

        String htmlBody = "<p>Dear " + contact.getFullName() + ",</p>" +
                "<p>Please find attached the Purchase Order.</p>" +
                "<p>To confirm this purchase order, click <a href=\"" + confirmLink + "\">here</a>.</p>" +
                "<p>To close this purchase order, click <a href=\"" + closeLink + "\">here</a>.</p>" +
                "<p>Best regards,<br>ERP System</p>";

        // ----------------------------
        // 5. Pošalji u JMS queue
        // ----------------------------
        PurchaseOrderNotification notification = new PurchaseOrderNotification();
        notification.setEmail(contact.getEmail());
        notification.setSubject("Purchase Order #" + po.getId());
        notification.setBody(htmlBody);
        notification.setReceiverId(contact.getId());
        notification.setAttachmentsBytes(List.of(pdfBytes));

        notification.setConfirmUrl(confirmLink);
        notification.setCloseUrl(closeLink);

        notificationSender.sendPurchaseOrderEmail(notification);

        return "Purchase order submitted successfully";
    }

    // ---------------- PDF generator u byte[] formatu ----------------
    private byte[] generatePurchaseOrderPdfBytes(PurchaseOrder po) {
        try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font fontTitle = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph("Purchase Order #" + po.getId(), fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            // Warehouse info
            WarehouseDto warehouse = warehouseWebClient.get()
                    .uri("/{id}", po.getWarehouseId())
                    .retrieve()
                    .bodyToMono(WarehouseDto.class)
                    .block();

            if (warehouse != null) {
                document.add(new Paragraph("Warehouse: " + warehouse.getName()));
                document.add(new Paragraph("Location: " + warehouse.getLocation()));
            }

            // Supplier info
            document.add(new Paragraph("Supplier: " + po.getSupplier().getName()));
            if (!po.getSupplier().getContacts().isEmpty()) {
                SupplierContact contact = po.getSupplier().getContacts().get(0);
                document.add(new Paragraph("Contact: " + contact.getFullName()));
                document.add(new Paragraph("Email: " + contact.getEmail()));
                document.add(new Paragraph("Phone: " + contact.getPhone()));
            }

            document.add(new Paragraph("Submitted At: " + po.getSubmittedAt()));
            document.add(new Paragraph(" "));

            // Tabela proizvoda
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.addCell("Product");
            table.addCell("Brand");
            table.addCell("Quantity");
            table.addCell("Unit");
            table.addCell("Unit Price");
            table.addCell("Total");

            for (PurchaseOrderItem item : po.getItems()) {
                ProductDto product = productWebClient.get()
                        .uri("/{id}", item.getProductId())
                        .retrieve()
                        .bodyToMono(ProductDto.class)
                        .block();

                if (product == null) continue;

                table.addCell(product.getName());
                table.addCell(product.getBrand());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(product.getUnitOfMeasure());
                table.addCell(String.valueOf(item.getPurchasePrice()));
                BigDecimal total = item.getPurchasePrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                table.addCell(String.valueOf(total));
            }

            document.add(table);
            document.add(new Paragraph("Generated by ERP System"));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    @Override
    @Transactional
    public String confirmPurchaseOrder(Long purchaseOrderId) {
        PurchaseOrder po = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));

        if (po.getStatus() != PurchaseOrderStatus.SUBMITTED) {
            throw new RuntimeException("Only submitted Purchase Orders can be confirmed");
        }

        po.setStatus(PurchaseOrderStatus.CONFIRMED);
        purchaseOrderRepository.save(po);

        return "Purchase Order #" + po.getId() + " confirmed successfully.";
    }

    @Override
    @Transactional
    public String closePurchaseOrder(Long purchaseOrderId) {
        PurchaseOrder po = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));

        if (po.getStatus() != PurchaseOrderStatus.CONFIRMED) {
            throw new RuntimeException("Only confirmed Purchase Orders can be closed");
        }

        po.setStatus(PurchaseOrderStatus.CLOSED);
        purchaseOrderRepository.save(po);

        return "Purchase Order #" + po.getId() + " closed successfully.";
    }


    @Override
    public void receivePurchaseOrder(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Purchase Order not found")
                );

        if (po.getStatus() != PurchaseOrderStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Only CONFIRMED purchase orders can be marked as RECEIVED"
            );
        }

        po.setStatus(PurchaseOrderStatus.RECEIVED);
        purchaseOrderRepository.save(po);
    }

    @Override
    public PurchaseOrderDto getPurchaseOrderById(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found"));

        PurchaseOrderDto dto = new PurchaseOrderDto();
        dto.setId(po.getId());

        dto.setItems(
                po.getItems().stream().map(item -> {
                    PurchaseOrderItemDto i = new PurchaseOrderItemDto();
                    i.setProductId(item.getProductId());
                    i.setQuantity(item.getQuantity());
                    i.setPurchasePrice(item.getPurchasePrice());
                    return i;
                }).collect(Collectors.toList())
        );

        return dto;
    }


}
