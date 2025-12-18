package com.dimitrijevic175.sales_service.repository;

import com.dimitrijevic175.sales_service.domain.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {

}
