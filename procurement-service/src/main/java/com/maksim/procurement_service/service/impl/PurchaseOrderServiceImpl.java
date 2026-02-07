package com.maksim.procurement_service.service.impl;

import com.maksim.procurement_service.configuration.ProductClient;
import com.maksim.procurement_service.configuration.WarehouseClient;
import com.maksim.procurement_service.domain.*;
import com.maksim.procurement_service.dto.*;
import com.maksim.procurement_service.listener.NotificationSender;
import com.maksim.procurement_service.mapper.PurchaseOrderMapper;
import com.maksim.procurement_service.repository.*;
import com.maksim.procurement_service.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
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
    private final WarehouseClient warehouseClient;
    private final ProductClient productClient;
    private final NotificationSender notificationSender;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private static final Logger logger = LogManager.getLogger(PurchaseOrderServiceImpl.class);


    @Override
    public Page<PurchaseOrderDto> getAllPurchaseOrders(Pageable pageable) {
        logger.info("Fetching all purchase orders");
        Page<PurchaseOrderDto> page = purchaseOrderRepository.findAll(pageable)
                .map(purchaseOrderMapper::toDto);
        logger.debug("Fetched {} purchase orders", page.getNumberOfElements());
        return page;
    }

    @Override
    public PurchaseOrderResponseDto createAutoPurchaseOrder(CreatePurchaseOrderRequestDto request) {
        logger.info("Creating auto purchase order for supplierId={} and warehouseId={}", request.getSupplierId(), request.getWarehouseId());

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> {
                    logger.error("Supplier not found, id={}", request.getSupplierId());
                    return new RuntimeException("Supplier not found");
                });

        List<LowStockItemDto> lowStockItems = warehouseClient.getLowStock(request.getWarehouseId());


        logger.debug("Fetched {} low-stock items from warehouseId={}", lowStockItems != null ? lowStockItems.size() : 0, request.getWarehouseId());

        PurchaseOrder po = new PurchaseOrder();
        po.setWarehouseId(request.getWarehouseId());
        po.setSupplier(supplier);
        po.setStatus(PurchaseOrderStatus.DRAFT);

        for (LowStockItemDto lowStock : lowStockItems) {
            ProductInfoDto productInfo = productClient.getProductById(lowStock.getProductId());


            if (productInfo == null) {
                logger.warn("Product info not found for productId={}", lowStock.getProductId());
                continue;
            }

            int orderQuantity = productInfo.getMaxQuantity() - lowStock.getQuantity();
            if (orderQuantity <= 0) {
                logger.debug("Skipping productId={} because orderQuantity <= 0", lowStock.getProductId());
                continue;
            }

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setPurchaseOrder(po);
            item.setProductId(lowStock.getProductId());
            item.setQuantity(orderQuantity);
            item.setPurchasePrice(productInfo.getPurchasePrice());
            po.getItems().add(item);
        }

        po = purchaseOrderRepository.save(po);
        logger.info("Auto purchase order created with id={}", po.getId());

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
            ProductInfoDto prodInfo = productClient.getProductById(item.getProductId());
            dto.setProductName(prodInfo != null ? prodInfo.getName() : null);
            return dto;
        }).collect(Collectors.toList()));

        logger.debug("Purchase order response prepared for id={}", po.getId());

        return response;
    }

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
        logger.info("Submitting purchase order id={}", purchaseOrderId);
        PurchaseOrder po = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> {
                    logger.error("Purchase order not found id={}", purchaseOrderId);
                    return new RuntimeException("Purchase Order not found");
                });

        if (po.getStatus() != PurchaseOrderStatus.DRAFT) {
            logger.error("Purchase order id={} already submitted or closed. Status={}", purchaseOrderId, po.getStatus());
            throw new RuntimeException("Purchase order already submitted or closed");
        }

        List<LowStockItemDto> lowStockItems = warehouseClient.getLowStock(po.getWarehouseId());


        logger.debug("Fetched {} low-stock items for PO id={}", lowStockItems != null ? lowStockItems.size() : 0, po.getId());

        if (lowStockItems != null) {
            for (LowStockItemDto item : lowStockItems) {
                boolean exists = po.getItems().stream()
                        .anyMatch(i -> i.getProductId().equals(item.getProductId()));
                if (exists) continue;

                ProductInfoDto product = productClient.getProductById(item.getProductId());


                if (product == null) {
                    logger.warn("Product info not found for productId={} during submit", item.getProductId());
                    continue;
                }

                PurchaseOrderItem poItem = new PurchaseOrderItem();
                poItem.setPurchaseOrder(po);
                poItem.setProductId(item.getProductId());
                poItem.setQuantity(product.getMaxQuantity());
                poItem.setPurchasePrice(product.getPurchasePrice());
                po.getItems().add(poItem);
            }
        }

        po.setStatus(PurchaseOrderStatus.SUBMITTED);
        po.setSubmittedAt(LocalDateTime.now());
        purchaseOrderRepository.save(po);
        logger.info("Purchase order id={} submitted successfully", po.getId());

        byte[] pdfBytes = generatePurchaseOrderPdfBytes(po);
        logger.info("Generated PDF for purchase order id={}", po.getId());

        return "Purchase order submitted successfully";
    }

    @Override
    @Transactional
    public String confirmPurchaseOrder(Long purchaseOrderId) {
        logger.info("Confirming purchase order id={}", purchaseOrderId);
        PurchaseOrder po = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> {
                    logger.error("Purchase order not found id={}", purchaseOrderId);
                    return new RuntimeException("Purchase Order not found");
                });

        if (po.getStatus() != PurchaseOrderStatus.SUBMITTED) {
            logger.error("Cannot confirm purchase order id={}. Status={}", purchaseOrderId, po.getStatus());
            throw new RuntimeException("Only submitted Purchase Orders can be confirmed");
        }

        po.setStatus(PurchaseOrderStatus.CONFIRMED);
        purchaseOrderRepository.save(po);
        logger.info("Purchase order id={} confirmed successfully", po.getId());

        return "Purchase Order #" + po.getId() + " confirmed successfully.";
    }

    @Override
    @Transactional
    public String closePurchaseOrder(Long purchaseOrderId) {
        logger.info("Closing purchase order id={}", purchaseOrderId);
        PurchaseOrder po = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> {
                    logger.error("Purchase order not found id={}", purchaseOrderId);
                    return new RuntimeException("Purchase Order not found");
                });

        if (po.getStatus() != PurchaseOrderStatus.CONFIRMED) {
            logger.error("Cannot close purchase order id={}. Status={}", purchaseOrderId, po.getStatus());
            throw new RuntimeException("Only confirmed Purchase Orders can be closed");
        }

        po.setStatus(PurchaseOrderStatus.CLOSED);
        purchaseOrderRepository.save(po);
        logger.info("Purchase order id={} closed successfully", po.getId());

        return "Purchase Order #" + po.getId() + " closed successfully.";
    }

    @Override
    public void receivePurchaseOrder(Long id) {
        logger.info("Receiving purchase order id={}", id);
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Purchase order not found id={}", id);
                    return new RuntimeException("Purchase Order not found");
                });

        if (po.getStatus() != PurchaseOrderStatus.CONFIRMED) {
            logger.error("Cannot mark PO id={} as RECEIVED. Status={}", id, po.getStatus());
            throw new IllegalStateException("Only CONFIRMED purchase orders can be marked as RECEIVED");
        }

        po.setStatus(PurchaseOrderStatus.RECEIVED);
        purchaseOrderRepository.save(po);
        logger.info("Purchase order id={} marked as RECEIVED", id);
    }

    @Override
    public PurchaseOrderDto getPurchaseOrderById(Long id) {
        logger.info("Fetching purchase order id={}", id);
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Purchase order not found id={}", id);
                    return new RuntimeException("Purchase order not found");
                });

        PurchaseOrderDto dto = new PurchaseOrderDto();
        dto.setId(po.getId());
        dto.setItems(po.getItems().stream().map(item -> {
            PurchaseOrderItemDto i = new PurchaseOrderItemDto();
            i.setProductId(item.getProductId());
            i.setQuantity(item.getQuantity());
            i.setPurchasePrice(item.getPurchasePrice());
            return i;
        }).collect(Collectors.toList()));

        logger.debug("Fetched purchase order id={} with {} items", id, dto.getItems().size());
        return dto;
    }

    public byte[] generatePurchaseOrderPdfBytes(PurchaseOrder po) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font fontTitle = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph("Purchase Order #" + po.getId(), fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            WarehouseDto warehouse = warehouseClient.getWarehouseById(po.getWarehouseId());


            if (warehouse != null) {
                document.add(new Paragraph("Warehouse: " + warehouse.getName()));
                document.add(new Paragraph("Location: " + warehouse.getLocation()));
            }

            document.add(new Paragraph("Supplier: " + po.getSupplier().getName()));
            if (!po.getSupplier().getContacts().isEmpty()) {
                SupplierContact contact = po.getSupplier().getContacts().get(0);
                document.add(new Paragraph("Contact: " + contact.getFullName()));
                document.add(new Paragraph("Email: " + contact.getEmail()));
                document.add(new Paragraph("Phone: " + contact.getPhone()));
            }

            document.add(new Paragraph("Submitted At: " + po.getSubmittedAt()));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.addCell("Product");
            table.addCell("Brand");
            table.addCell("Quantity");
            table.addCell("Unit");
            table.addCell("Unit Price");
            table.addCell("Total");

            for (PurchaseOrderItem item : po.getItems()) {
                ProductDto product = productClient.getFullProductById(item.getProductId());


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
            logger.debug("PDF generated for purchase order id={}", po.getId());
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("Error generating PDF for purchase order id={}: {}", po.getId(), e.getMessage());
            throw new RuntimeException("Error generating PDF", e);
        }
    }


}
