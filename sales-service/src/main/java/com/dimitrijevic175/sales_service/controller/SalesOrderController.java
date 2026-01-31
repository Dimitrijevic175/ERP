package com.dimitrijevic175.sales_service.controller;

import com.dimitrijevic175.sales_service.dto.CreateSalesOrderRequestDto;
import com.dimitrijevic175.sales_service.dto.SalesOrderDto;
import com.dimitrijevic175.sales_service.dto.SalesOrderResponseDto;
import com.dimitrijevic175.sales_service.service.SalesOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @PostMapping
    public ResponseEntity<SalesOrderResponseDto> createOrder(
            @RequestBody CreateSalesOrderRequestDto request
    ) {
        SalesOrderResponseDto response = salesOrderService.createSalesOrder(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        salesOrderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<SalesOrderDto>> getAllSalesOrders(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<SalesOrderDto> orders = salesOrderService.getAllSalesOrders(pageable);
        return ResponseEntity.ok(orders);
    }
}
