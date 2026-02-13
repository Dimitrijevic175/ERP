package com.dimitrijevic175.sales_service.service.impl;

import com.dimitrijevic175.sales_service.configuration.WarehouseServiceClient;
import com.dimitrijevic175.sales_service.domain.*;
import com.dimitrijevic175.sales_service.dto.*;
import com.dimitrijevic175.sales_service.repository.*;
import com.dimitrijevic175.sales_service.service.SalesOrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
@RequiredArgsConstructor
@Transactional
public class SalesOrderServiceImpl implements SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final CustomerRepository customerRepository;
    private final WarehouseServiceClient warehouseWebClient;
    private static final Logger logger = LogManager.getLogger(SalesOrderServiceImpl.class);


    @Override
    public SalesOrderResponseDto createSalesOrder(CreateSalesOrderRequestDto request) {
        logger.info("Creating sales order for customerId={}", request.getCustomerId());

        // 1️⃣ Dohvati customer
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> {
                    logger.error("Customer not found with id={}", request.getCustomerId());
                    return new RuntimeException("Customer not found");
                });
        logger.debug("Customer found: {}", customer.getEmail());

        // 2️⃣ Proveri zalihe i dohvati warehouse ID
        CheckAvailabilityRequestDto availabilityRequest = new CheckAvailabilityRequestDto();
        availabilityRequest.setItems(request.getItems().stream()
                .map(i -> new CheckAvailabilityRequestDto.Item(i.getProductId(), i.getQuantity()))
                .collect(Collectors.toList())
        );

        CheckAvailabilityResponseDto response = warehouseWebClient.checkAvailability(availabilityRequest);

        if (response.getWarehouseId() == null) {
            throw new RuntimeException("No warehouse can fulfill the requested order");
        }
        logger.debug("Warehouse {} can fulfill the order", response.getWarehouseId());

        // 3️⃣ Kreiraj SalesOrder
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setCustomer(customer);
        salesOrder.setStatus(SalesOrderStatus.CREATED);
        salesOrder.setWarehouseId(response.getWarehouseId());

        List<SalesOrderItem> items = request.getItems().stream().map(i -> {
            SalesOrderItem item = new SalesOrderItem();
            item.setSalesOrder(salesOrder);
            item.setProductId(i.getProductId());
            item.setQuantity(i.getQuantity());
            item.setDiscount(i.getDiscount());
            item.setTaxRate(i.getTaxRate());
            item.setSellingPrice(i.getSellingPrice());
            return item;
        }).collect(Collectors.toList());

        salesOrder.setItems(items);
//        salesOrderRepository.save(salesOrder);
        SalesOrder savedOrder = salesOrderRepository.save(salesOrder);


        logger.info("Sales order {} created with {} items", savedOrder.getId(), items.size());

        // 4️⃣ Kreiraj dispatch note u warehouse servisu
        warehouseWebClient.createDispatchNote(new DispatchNoteRequestDto(savedOrder));

        logger.info("Dispatch note created for sales order {}", savedOrder.getId());

        // 5️⃣ Mapiraj u response DTO
        SalesOrderResponseDto responseDto = new SalesOrderResponseDto();
        responseDto.setId(savedOrder.getId());
        responseDto.setCustomerId(customer.getId());
        responseDto.setWarehouseId(response.getWarehouseId());
        responseDto.setStatus(salesOrder.getStatus().name());
        responseDto.setCreatedAt(savedOrder.getCreatedAt());
        responseDto.setItems(items.stream().map(i -> {
            SalesOrderResponseDto.SalesOrderItemDto dto = new SalesOrderResponseDto.SalesOrderItemDto();
            dto.setProductId(i.getProductId());
            dto.setQuantity(i.getQuantity());
            dto.setDiscount(i.getDiscount());
            dto.setTaxRate(i.getTaxRate());
            dto.setSellingPrice(i.getSellingPrice());
            return dto;
        }).collect(Collectors.toList()));

        logger.debug("Sales order response prepared for orderId={}", salesOrder.getId());
        return responseDto;
    }

    @Override
    public void cancelOrder(Long orderId) {
        logger.info("Canceling sales order {}", orderId);

        SalesOrder order = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == SalesOrderStatus.CLOSED) {
            throw new RuntimeException("Order already canceled");
        }

        DispatchNoteDto dispatchNote =
                warehouseWebClient.getDispatchNoteBySalesOrderId(orderId);

        if (dispatchNote != null) {
            if (dispatchNote.getStatus() != DispatchNoteStatusDto.DRAFT) {
                throw new RuntimeException(
                        "Order cannot be canceled because goods are already dispatched"
                );
            }

            warehouseWebClient.rollbackDispatchNote(dispatchNote.getId());
            logger.info("Warehouse rollback successful for dispatchNote {}", dispatchNote.getId());
        }

        order.setStatus(SalesOrderStatus.CLOSED);
        order.setClosedAt(LocalDateTime.now());
        salesOrderRepository.save(order);

        logger.info("Order {} canceled successfully", orderId);
    }

    @Override
    public Page<SalesOrderDto> getAllSalesOrders(Pageable pageable) {
        logger.debug("Fetching all sales orders, page={}", pageable.getPageNumber());
        return salesOrderRepository.findAll(pageable)
                .map(this::toDto);
    }

    private SalesOrderDto toDto(SalesOrder order) {
        SalesOrderDto dto = new SalesOrderDto();
        dto.setId(order.getId());
        dto.setCustomerId(order.getCustomer().getId());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setClosedAt(order.getClosedAt());

        if (order.getItems() != null) {
            List<SalesOrderItemDto> itemDtos = order.getItems().stream().map(item -> {
                SalesOrderItemDto itemDto = new SalesOrderItemDto();
                itemDto.setProductId(item.getProductId());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setUnitPrice(item.getSellingPrice());
                return itemDto;
            }).toList();
            dto.setItems(itemDtos);

            BigDecimal totalAmount = order.getItems().stream()
                    .map(item -> item.getSellingPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            dto.setTotalAmount(totalAmount);
        } else {
            dto.setTotalAmount(BigDecimal.ZERO);
        }

        logger.debug("Mapped order {} to DTO with totalAmount={}", order.getId(), dto.getTotalAmount());
        return dto;
    }

}
