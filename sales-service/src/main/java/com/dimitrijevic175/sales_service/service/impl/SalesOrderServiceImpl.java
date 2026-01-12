package com.dimitrijevic175.sales_service.service.impl;

import com.dimitrijevic175.sales_service.domain.*;
import com.dimitrijevic175.sales_service.dto.*;
import com.dimitrijevic175.sales_service.repository.*;
import com.dimitrijevic175.sales_service.service.SalesOrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SalesOrderServiceImpl implements SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final CustomerRepository customerRepository;
    private final WebClient warehouseWebClient; // pozivi warehouse servisu

    @Override
    public SalesOrderResponseDto createSalesOrder(CreateSalesOrderRequestDto request) {

        // 1️⃣ Dohvati customer
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // 2️⃣ Proveri zalihe i dohvati warehouse ID koji može da ispuni porudžbinu
        CheckAvailabilityRequestDto availabilityRequest = new CheckAvailabilityRequestDto();
        availabilityRequest.setItems(request.getItems().stream()
                .map(i -> new CheckAvailabilityRequestDto.Item(i.getProductId(), i.getQuantity()))
                .collect(Collectors.toList())
        );

        CheckAvailabilityResponseDto response = warehouseWebClient.post()
                .uri("/warehouses/checkAvailability")
                .bodyValue(availabilityRequest)
                .retrieve()
                .bodyToMono(CheckAvailabilityResponseDto.class)
                .block();

        if (response == null || response.getWarehouseId() == null) {
            throw new RuntimeException("No warehouse can fulfill the requested order");
        }

        Long warehouseId = response.getWarehouseId();

        // 3️⃣ Kreiraj SalesOrder
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setCustomer(customer);
        salesOrder.setStatus(SalesOrderStatus.CREATED);
        salesOrder.setWarehouseId(warehouseId);

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

        salesOrderRepository.save(salesOrder);

        // 4️⃣ Kreiraj dispatch note u warehouse servisu
        DispatchNoteRequestDto dispatchNoteRequest = new DispatchNoteRequestDto(salesOrder);
        warehouseWebClient.post()
                .uri("/dispatch-notes")
                .bodyValue(dispatchNoteRequest)
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        // 5️⃣ Posalji potvrdu kupcu (simulacija)
        System.out.println("Email sent to customer " + customer.getEmail() + " for order " + salesOrder.getId());

        // 6️⃣ Mapiraj u response DTO
        SalesOrderResponseDto response1 = new SalesOrderResponseDto();
        response1.setId(salesOrder.getId());
        response1.setCustomerId(customer.getId());
        response1.setWarehouseId(warehouseId);
        response1.setStatus(salesOrder.getStatus().name());
        response1.setCreatedAt(salesOrder.getCreatedAt());
        response1.setItems(items.stream().map(i -> {
            SalesOrderResponseDto.SalesOrderItemDto dto = new SalesOrderResponseDto.SalesOrderItemDto();
            dto.setProductId(i.getProductId());
            dto.setQuantity(i.getQuantity());
            dto.setDiscount(i.getDiscount());
            dto.setTaxRate(i.getTaxRate());
            dto.setSellingPrice(i.getSellingPrice());
            return dto;
        }).collect(Collectors.toList()));

        return response1;
    }
}
