package com.dimitrijevic175.sales_service.service;

import com.dimitrijevic175.sales_service.dto.CreateSalesOrderRequestDto;
import com.dimitrijevic175.sales_service.dto.SalesOrderDto;
import com.dimitrijevic175.sales_service.dto.SalesOrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SalesOrderService {

    SalesOrderResponseDto createSalesOrder(CreateSalesOrderRequestDto request);
    void cancelOrder(Long orderId);
    Page<SalesOrderDto> getAllSalesOrders(Pageable pageable);

}
