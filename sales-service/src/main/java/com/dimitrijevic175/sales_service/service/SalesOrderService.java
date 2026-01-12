package com.dimitrijevic175.sales_service.service;

import com.dimitrijevic175.sales_service.dto.CreateSalesOrderRequestDto;
import com.dimitrijevic175.sales_service.dto.SalesOrderResponseDto;

public interface SalesOrderService {

    SalesOrderResponseDto createSalesOrder(CreateSalesOrderRequestDto request);
}
