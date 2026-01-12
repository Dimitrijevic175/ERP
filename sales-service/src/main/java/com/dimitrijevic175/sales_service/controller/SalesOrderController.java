package com.dimitrijevic175.sales_service.controller;

import com.dimitrijevic175.sales_service.dto.CreateSalesOrderRequestDto;
import com.dimitrijevic175.sales_service.dto.SalesOrderResponseDto;
import com.dimitrijevic175.sales_service.service.SalesOrderService;
import lombok.RequiredArgsConstructor;
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
}
